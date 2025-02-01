const express = require("express");
const userRoute = express.Router();
const User = require("../models/User");
const { verifyHash, genHash } = require("../utils/passwordHash");

userRoute.route("/:user").get((req, res, next) => {
  User.findOne({ _id: req.params.user })
    .then((user) => {
      if (user) {
        res.status(200).json(user);
      } else {
        res.status(401).json({ result: "User not found" });
      }
    })
    .catch((err) => {
      res.status(401).json({ result: "User not found",errorMsg:err });
    });
})

userRoute.route("/login").post((req, res, next) => {
  User.findOne({ email: req.body.email })
    .then((user) => {
      passwordHash = user.password;
      const match = verifyHash(passwordHash, req.body.password);
      if (match) {
        res.status(200).json({
          status: true,
          _id: user._id,
        });
      } else {
        res.status(401).json({ result: "Unauthorized User." });
      }
    })
    .catch((err) => {
      console.log(err);
      res.status(400);
    });
});

userRoute.route("/register").post((req, res, next) => {

  User.findOne({ email: req.body.email }).then(data => {
    if (data) {
      return res.status(201).json({ message: "Email is already registered." });
    } else {
      User.create({
        name: req.body.name,
        email: req.body.email,
        password: genHash(req.body.confirmPassword),
        created_on: Date.now()
      })
        .then((response) => {
          return res.status(200).json({ message: "User created." });
        })
        .catch((error) => {
          res.status(500).json({
            error: error,
          });
        });
    }
  })

});

module.exports = userRoute;
