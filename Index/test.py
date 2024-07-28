import requests
import json

API_KEY = "eOoT9Yyjppt6NZjrMjlQa9pA"
SECRET_KEY = "O5VGlicNWMJre1JRBZRxzA2naggztbdn"

def GetAccessToeken():
    token_host = 'https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id={ak}&client_secret={sk}'.format(
      ak=API_KEY, sk=SECRET_KEY)
    header = {'Content-Type': 'application/json; charset=UTF-8'}
    response = requests.post(url=token_host, headers=header)
    content = response.json()
    access_token = content.get("access_token")
    return access_token
    
request_url = 'https://aip.baidubce.com/rpc/2.0/creation/v1/poem'  # 智能写诗
access_token = GetAccessToeken()
print(access_token)

msg = { "text": "风雪",
			"index": 0}
request_url = request_url + "?access_token=" + access_token
headers = {'content-type': 'application/json'}
response = requests.post(request_url, data=json.dumps(msg), headers=headers)
if response:
    result = response.json()
    print(result)


