from flask import Flask
from flask_sqlalchemy import SQLAlchemy
import json
from settings import app

db = SQLAlchemy(app)

class Book(db.Model):
    __tablename__ = 'books'
    id = db.Column(db.Integer,primary_key=True)
    name = db.Column(db.String(80),nullable=False)
    price = db.Column(db.Float,nullable=False)
    isbn = db.Column(db.Integer)

    def json(self):
        return {'name':self.name,'price':self.price,'isbn':self.isbn}

    def add_book(_name,_price,_isbn):
        new_book = Book(name=_name,price=_price,isbn=_isbn)
        db.session.add(new_book)
        db.session.commit()

    def get_all_books(self):
        return [Book.json(book) for book in Book.query.all()]

    def get_book(_isbn):
        return Book.json(Book.query.filter_by(isbn=_isbn).first())

    def delete_book(_isbn):
        is_sucessful = Book.query.filter_by(isbn=_isbn).delete()
        db.session.commit()
        return bool(is_sucessful)

    def update_book_price(_isbn,_price):
        book_to_update = Book.query.filter_by(isbn=_isbn).first()
        book_to_update.price = _price
        db.session.commit()

    def update_book_name(_isbn,_name):
        book_to_update = Book.query.filter_by(isbn=_isbn).first()
        book_to_update.name = _name
        db.session.commit()

    def replace_book(_isbn,_name,_price):
        book_to_replace = Book.query.filter_by(isbn=_isbn).first()
        book_to_replace.price = _price
        book_to_replace.name  = _name
        db.session.commit()

    def __repr__(self):
        book_object = {
            'name':self.name,
            'price':self.price,
            'isbn':self.isbn
        }
        return json.dumps(book_object)