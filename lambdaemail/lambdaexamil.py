import json
import boto3

ses = boto3.client('ses','us-east-1')

email_from = 'kali.tummala@gmail.com'
reciepients = ['srtummal@amazon.com']
email_to = ', '.join(reciepients)
email_cc = 'srtummal@amazon.com'
emaiL_subject = 'REMAINDER:- SUBMIT YOUR TIMESHEET'
email_body = 'Hi Team,\n\n Please submit your time sheet and have a nice weekend. \n\n Sri'


def lambda_handler(event, context):
    response = ses.send_email(
        Source = email_from,
        Destination={
            'ToAddresses': [
                email_to,
            ],
            'CcAddresses': [
                email_cc,
            ]
        },
        Message={
            'Subject': {
                'Data': emaiL_subject
            },
            'Body': {
                'Text': {
                    'Data': email_body
                }
            }
        }
    )