from flask import Flask,jsonify,request,Response
from BookModel import *
import json
from test import *
from settings import *
import jwt,datetime
from UserModel import User
from functools import wraps

app = Flask(__name__)

app.config['SECRET_KEY'] = 'meow'

@app.route('/login',methods=['POST'])
def get_token():
    request_data = request.get_json()
    username = str(request_data['username'])
    password = str(request_data['password'])

    match = User.username_password_match(username,password)

    if match:
        expiration_date = datetime.datetime.utcnow() + datetime.timedelta(seconds=100)
        token = jwt.encode({'exp': expiration_date}, app.config['SECRET_KEY'], algorithm='HS256')
        return token
    else:
        return Response('',401,mimetype='application/json')

def token_required(f):
    @wraps(f)
    def wrapper(*args,**kwargs):
        token = request.args.get('token')
        try:
            jwt.decode(token, app.config['SECRET_KEY'])
            return f(*args, **kwargs)
        except:
            return jsonify({'error': 'Need a valid token to view this page'})
    return wrapper

#GET /books
@app.route('/books')
def get_books():
    return jsonify({'books':Book.get_all_books()})

@app.route('/books',methods=['POST'])
@token_required
def add_book():
    request_data = request.get_json()
    if(validBookObject(request_data)):
        Book.add_book(request_data['name'],request_data['price'],request_data['isbn'])
        response = Response("",201,mimetype='application/json')
        response.headers['Location'] = "/books/" + str(request_data['isbn'])
        return response
    else:
        invalidBookObjectErrorMsg = {
            "error": "Invalid book object passed in request",
            "helpString": "Data passed in similar to this {'name':'bookname'}"
        }
        response = Response(json.dumps(invalidBookObjectErrorMsg),status=400,mimetype='application/json')
        return response

@app.route('/books/<int:isbn>')
def get_book_by_isbn(isbn):
    return_value = Book.get_book(isbn)
    return jsonify(return_value)

@app.route('/books/<int:isbn>',methods=['PUT'])
@token_required
def replace_book(isbn):
    request_data = request.get_json()
    Book.replace_book(isbn,request_data['name'],request_data['price'])
    response = Response("",status=204)
    return response

@app.route('/books/<int:isbn>',methods=['PATCH'])
@token_required
def update_book(isbn):
    request_data = request.get_json()
    updated_book = {}
    if("name" in request_data):
        Book.update_book_price(isbn,request_data['price'])
    if("price" in request_data):
        Book.update_book_name(isbn, request_data['name'])

    response = Response("",status=204)
    response.headers['Location'] = "/books/" + str(isbn)
    return response


@app.route('/books/<int:isbn>',methods=['DELETE'])
@token_required
def delete_book(isbn):
    if(Book.delete_book(isbn)):
        response = Response("",status=204)
        return response
    else:
        response = Response("",status=404,mimetype='application/json')
        return response;

app.run(port=5000)