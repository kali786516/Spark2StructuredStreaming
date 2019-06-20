def validBookObject(bookObject):
    if ("name" in bookObject
            and "price" in bookObject
            and "isbn" in bookObject):
        return True
    else:
        return False

valid_object = {
    'name':'F',
    'price':6.99,
    'isbn': 12345343543444
}

missing_name = {
    'price':6.99,
    'isbn':1223454
}

missing_price = {
    'name':'F',
    'isbn':12133432312
}

missing_isbn = {
    'name':'F',
    'isbn':32423312312
}

empty_dict = {}