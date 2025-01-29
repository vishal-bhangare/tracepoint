const mongoose = require("mongoose");
const Schema = mongoose.Schema;

const Post = new Schema(
  {
    title: { type: String },
    description: { type: String },
    location: {
      type: { type: String },
      coordinates: [Number],
    },
    type: { type: Boolean },
    author: { type: String },
    images: { type: [String] },
    added_on: { type: Date },
  },
  {
    collection: "posts",
  }
);

module.exports = mongoose.model("Post", Post);
