# Jad Menkara
# server.py
# Extremeely basic server used to send values to the java game and update them based on POST requests
# from the right and left classifiers



from flask import Flask, request, jsonify
import json
app = Flask(__name__)

JSONOutput = { #contains current controls to be sent to the java game
    "left":
        {
            "punch": 0,
            "kick": 0,
            "special": 0,
            "moving": 0
        },
    "right":
        {
            "punch": 0,
            "kick": 0,
            "special": 0,
            "moving": 0
        },
        
    
}


#https://medium.com/@muhammadirfan92/creating-and-deploying-a-simple-flask-api-server-and-client-side-7d4f5690551

@app.route(rule="/api", methods = ["GET", "POST"])
def handle_request():
    global JSONOutput
    #this will be used for our get endpoint
    if request.method == "GET":
        return jsonify(JSONOutput)
    
    #this will be used for our post endpoint
    if request.method == "POST":
        
        #accessing the passed payload
        payload = request.get_json( )
        
        #updates the values based on what we send in our payload
        if payload["direction"] == 0:
            JSONOutput["left"]["punch"] = payload["punch"]
            JSONOutput["left"]["kick"] = payload["kick"]
            JSONOutput["left"]["special"] = payload["special"]
            JSONOutput["left"]["moving"] = payload["moving"]
        if payload["direction"] == 1:
            JSONOutput["right"]["punch"] = payload["punch"]
            JSONOutput["right"]["kick"] = payload["kick"]
            JSONOutput["right"]["special"] = payload["special"]
            JSONOutput["right"]["moving"] = payload["moving"]

        response = jsonify(JSONOutput)
        
        return response
    
# Running the API
if __name__ == "__main__": 
    app.run(host="0.0.0.0", debug=False, port = 5000)
    