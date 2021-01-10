import json
import requests
import sys

version = sys.argv[1]
token = sys.argv[2]

headers = {
    'Content-Type': 'application/json',
    'Authorization': f"Token {token}"
}
data = '''[
  {
    "command": "fibery.entity/query",
    "args": {
      "query": {
        "q/from": "FiberyUnofficial/Release",
        "q/select": ["fibery/id"],
        "q/where": ["=", ["FiberyUnofficial/name"], "$name"],
        "q/limit": 1
      },
      "params": {
          "$name": "0.14.0"
      }
    }
  }
]'''
data = data.replace("{version}", version)
input = requests.post('https://krossovochkin.fibery.io/api/commands', headers=headers, data=data).content

r = json.loads(input)

release_id = r[0]["result"][0]["fibery/id"]

data = '''[{"command":"fibery.entity/update","args":{"type":"FiberyUnofficial/Release","entity":{"FiberyUnofficial/Released":true,"fibery/id":"{id}"}}}]'''
data = data.replace("{id}", release_id)
response = requests.post('https://krossovochkin.fibery.io/api/commands', headers=headers, data=data).content