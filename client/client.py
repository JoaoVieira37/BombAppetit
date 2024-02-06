import requests
import json
import subprocess
import time
import os
import urllib3

def printHelp():
    print("Available commands:")
    print("info - asks the server to send a list of available restaurants")
    print("find <ID> - asks the server to send information about a specific restaurant")
    print("review - creates a review for a restaurant and sends it to the server")
    print("give - creates a request to give")
    print("vouchers - list available vouchers")
    print("quit - quits the application")

def createFindJson(name, username):
    findInfo = {}
    dictionary = {}
    dictionary["username"] = username

    currTime = round(time.time()*1000)
    currTimeStr = str(currTime)
    dictionary["timestamp"] = currTimeStr

    findInfo["info"] = dictionary

    with open(name, 'w') as f:
        json.dump(findInfo, f)

def createVouchersJson(name, username):
    findInfo = {}
    dictionary = {}
    dictionary["username"] = username

    currTime = round(time.time()*1000)
    currTimeStr = str(currTime)
    dictionary["timestamp"] = currTimeStr

    findInfo["info"] = dictionary

    with open(name, 'w') as f:
        json.dump(findInfo, f)


def createReviewJson(name, username):
    reviewInfo = {}
    dictionary = {}
    id = input("What restaurant ID are you reviewing?: ")
    if not (1 <= int(id) <= 2):
        print("Invalid ID: please choose a valid ID")
        id = input("What restaurant ID are you reviewing?: ")

    dictionary["restaurantID"] = id
    rating = input("What rating do you give this restaurant? (1-5): ")
    dictionary["rating"] = rating
    review = input("Please type your review of the restaurant?: ")
    dictionary["review"] = review
    dictionary["username"] = username

    currTime = round(time.time()*1000)
    currTimeStr = str(currTime)
    dictionary["timestamp"] = currTimeStr


    reviewInfo["info"] = dictionary

    with open(name, 'w') as f:
        json.dump(reviewInfo, f)

def createGiveJson(name, username):
    swapInfo = {}
    dictionary = {}
    idVoucher = input("What is the ID of the voucher you want to give to anothe user?: ")
    dictionary["voucherID"] = idVoucher
    user = input("What is the name of the user you want to give the voucher to?: ")
    dictionary["targetuser"] = user
    dictionary["username"] = username

    currTime = round(time.time()*1000)
    currTimeStr = str(currTime)
    dictionary["timestamp"] = currTimeStr

    swapInfo["info"] = dictionary

    with open(name, 'w') as f:
        json.dump(swapInfo, f)

def signJson(name, key):
    #runs java file SignReview.java, needs the following commands to work
    #mvn clean install
    #export PATH=$PATH:/media/sf_KALI_FOLDER/a14-joao-joao-miguel-dev/client/target/appassembler/bin
    #subprocess.run(["./target/appassembler/bin/signReview.bat", name, "info", key])
    os.system("C:\\Users\\jmigu\\Documents\\dev\\BombAppetit\\client\\target\\appassembler\\bin\\signReview.bat " + name + " info " + key)

def unprotectJson(name,outputname, key, type):
    #runs java file Unprotect.java, needs the following commankeyds to work
    #mvn clean install
    #export PATH=$PATH:/media/sf_KALI_FOLDER/3333333333333333/a14-joao-joao-miguel/client/target/appassembler/bin
    subprocess.run(["C:\\Users\\jmigu\\Documents\\dev\\BombAppetit\\client\\target\\appassembler\\bin\\unprotect.bat", name, outputname, key, "keys/serverPublic.pub", type])

def jsonDictToFile(dict,name):
    with open(name, 'w') as f:
        json.dump(dict, f)

def deleteFile(name):
    os.remove(name)

def jsonFileToDict(filename):
    with open(filename) as json_file:
        return json.load(json_file)

def printJsonPretty(responseJson):
    formattedJson = json.dumps(responseJson, indent=2)
    print(formattedJson)

def getUserPrivateKey(username):
    if(username == "user1"):
        return "keys/user1Private.key"
    elif(username == "user2"):
        return "keys/user2Private.key"
    elif(username == "user3"):
        return "keys/user3Private.key"
    else:
        print("invalid user! current users: user1, user2, user3")
        exit()

def getUserPublicKey(username):
    if(username == "user1"):
        return "keys/user1Public.pub"
    elif(username == "user2"):
        return "keys/user2Public.pub"
    elif(username == "user3"):
        return "keys/user3Public.pub"




#Execution start
cert_path="server.crt"
#link = "https://BombAppetit:8443/"
link = "http://localhost:8443/"
s = requests.Session()

username = input("Username: ")

privateKey = getUserPrivateKey(username)
publicKey = getUserPublicKey(username)

while(True):
    commandFull = input("Command: ")
    command = commandFull.split(" ")[0]


    if(command == "info"):
        headers = {'Accept': 'application/json'} #tells server to send json
        response = s.get(link + "info", headers=headers,verify=cert_path)
        printJsonPretty(response.json())
    

    elif(command == "find"):
        if not (1 <= int(commandFull.split(" ")[1]) <= 2):
            print("Invalid ID: please choose a valid ID [1, 2]")
        else:
            createFindJson("data.json", username)
            signJson("data.json", privateKey)
            jsonDict = jsonFileToDict("data.json")
            print(jsonDict)
            headers = {'Accept': 'application/json'} #tells server to send json
            response = s.post(link + "find" + "/" + commandFull.split(" ")[1], json=jsonDict, headers=headers,verify=cert_path)
            

            #unencrypt and remove security
            jsonDictToFile(response.json(),"data.json")
            unprotectJson("data.json", "output.json", privateKey, "find")
            jsonDict = jsonFileToDict("output.json")
            deleteFile("data.json")
            deleteFile("output.json")
            printJsonPretty(jsonDict)
    
    elif(command == "review"):
        createReviewJson("review.json", username)
        signJson("review.json", privateKey)
        jsonDict = jsonFileToDict("review.json")
        
        response = s.post(link + "add/review" , json=jsonDict,verify=cert_path)
        deleteFile("review.json")
    

    elif(command == "give"):
        createGiveJson("give.json", username)
        signJson("give.json", privateKey)
        
        jsonDict = jsonFileToDict("give.json")
        response = s.post(link + "give" , json=jsonDict,verify=cert_path)
        print(response.text)
        deleteFile("give.json")

    elif(command == "vouchers"):
        createFindJson("data.json", username)
        signJson("data.json", privateKey)
        jsonDict = jsonFileToDict("data.json")

        headers = {'Accept': 'application/json'} #tells server to send json
        response = s.post(link + "vouchers", json=jsonDict, headers=headers,verify=cert_path)
#
        #unencrypt and remove security
        jsonDictToFile(response.json(),"data.json")
        unprotectJson("data.json", "output.json", privateKey, "voucher")
        jsonDict = jsonFileToDict("output.json")
        deleteFile("data.json")
        deleteFile("output.json")
        printJsonPretty(jsonDict)
    

    elif(command == "help"):
        printHelp()
    elif(command == "quit" or command == "exit"):
        exit()
    else:
        print("Error, not a valid command, type help for a list of available commands")
        continue


    #print(response.text)
