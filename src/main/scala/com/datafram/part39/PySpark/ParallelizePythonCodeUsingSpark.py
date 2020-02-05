import os
import sys

sc=SparkContext(conf=Conf)

data=sc.textfile("file.txt")

def word_tokenizer(x):
    import ntlk
    return ntlk.word_tokenizer(x)


def post_tag(x)   :
    import ntlk
    return ntlk.word_tokenizer


words=data.flatMap(word_tokenizer)

pos_words.map(post_tag)