from flask import Flask, request, jsonify
from flask_pymongo import PyMongo
from flask_cors import CORS

app = Flask(__name__)
CORS(app)

app.config["MONGO_URI"] = "mongodb://localhost:27017/lms"
mongo = PyMongo(app)

@app.route("/")
def home():
    return "Flask backend is running"

@app.route("/register", methods=["POST"])
def register():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")
    user_type = data.get("user_type")

    if mongo.db.users.find_one({"username": username}):
        return jsonify({"message": "Username already exists"}), 409

    mongo.db.users.insert_one({
        "username": username,
        "password": password,
        "type": user_type
    })
    return jsonify({"message": "Registration successful"}), 201

@app.route("/login", methods=["POST"])
def login():
    data = request.get_json()
    username = data.get("username")
    password = data.get("password")

    user = mongo.db.users.find_one({"username": username})
    if user and user["password"] == password:
        return jsonify({
            "message": "Login successful",
            "role": user["type"]
        }), 200
    else:
        return jsonify({"message": "Invalid username or password"}), 401

@app.route('/books', methods=['GET'])
def get_books():
    books = mongo.db.books.find()
    book_list = []
    for book in books:
        book_list.append({
            'id': str(book['_id']),
            'title': book['title'],
            'author': book['author'],
            'available': book.get('available', True)
        })
    return jsonify(book_list)

@app.route('/books', methods=['POST'])
def add_book():
    data = request.get_json()
    title = data.get("title")
    author = data.get("author")

    if not title or not author:
        return jsonify({"message": "Title and author required"}), 400

    mongo.db.books.insert_one({
        "title": title,
        "author": author,
        "available": True
    })

    return jsonify({"message": "Book added successfully"}), 201



@app.route("/api/users", methods=["GET"])
def get_users():
    users = mongo.db.users.find()
    output = []
    for user in users:
        output.append({
            "id": str(user["_id"]),
            "username": user["username"],
            "type": user["type"]
        })
    return jsonify(output)

if __name__ == "__main__":
    app.run(debug=True)

