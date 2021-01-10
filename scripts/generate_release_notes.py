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
        "q/select": {
            "user/Tasks" : {
                "q/from": "user/Tasks",
                "q/select": ["fibery/public-id", "FiberyUnofficial/Name"],
                "q/limit": "q/no-limit"
            },
            "user/Bugs": {
                "q/from": "user/Bugs",
                "q/select": ["fibery/public-id", "FiberyUnofficial/name"],
                "q/limit": "q/no-limit"
            }
        },
        "q/limit": "q/no-limit",
        "q/where": ["=", ["FiberyUnofficial/name"], "$name"]
	  },
      "params": {
          "$name": "{version}"
      }
    }
  }
]'''
data = data.replace("{version}", version)
input = requests.post('https://krossovochkin.fibery.io/api/commands', headers=headers, data=data).content

r = json.loads(input)

print("Features and improvements:")
print("\n".join(list(map(lambda x: f"- #{x['fibery/public-id']} {x['FiberyUnofficial/Name']}", r[0]["result"][0]["user/Tasks"]))))
print("")
print("Fixed issues:")
print("\n".join(list(map(lambda x: f"- #{x['fibery/public-id']} {x['FiberyUnofficial/name']}", r[0]["result"][0]["user/Bugs"]))))