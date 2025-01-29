const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");
const bodyParser = require("body-parser");
const createError = require("http-errors");
require("dotenv").config();

const uri = process.env.DB_URI;

mongoose
  .connect(uri)
  .then((res) => {
    console.log(`Connected to Database : "${res.connections[0].name}"`);
  })
  .catch((err) => {
    console.error("Error connecting to mongo", err.reason);
  });

const app = express();

const postRoute = require("./routes/post.route");
const userRoute = require("./routes/user.route");
app.use(cors());
app.use(bodyParser.json());
app.use(
  bodyParser.urlencoded({
    extended: false,
  })
);
app.use("/api/post", postRoute);
app.use("/api/user", userRoute);

const port = process.env.PORT || 4000;
app.listen(port, () => {
  console.log("Server is live on port " + port);
});

app.use((req, res, next) => {
  next(createError(404));
});
app.use(function (err, req, res, next) {
  console.error(err.message);
  if (!err.statusCode) err.statusCode = 500;

  res.status(err.statusCode).send(err.message);
});