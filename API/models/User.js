const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const User = new Schema(
  {
    name: { type: String },
    email: { type: String },
    password: { type: String },
    created_on: { type: Date }
  },
  {
    collection: "users",
  }
);
module.exports = mongoose.model("User", User);
