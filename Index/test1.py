from flask import Flask
from flask import request
import test

app = Flask(__name__)

'''@app.route("/index",methods=['GET', 'POST'])
def index():
     return '<form action = "http://localhost:5000" method = "post"></form>'
'<a href="/test"><button onclick="">进入测试</button></a><a href="/test">'
'''
    

@app.route("/test",methods=['GET'])
def test():
    test.run()
    return 
'<form action = "http://localhost:5000" method = "get"></form><a href="/test"><button onclick="">进入测试</button></a>'
if __name__ == '__main__':
    app.run(debug=True)