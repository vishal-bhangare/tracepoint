const express = require("express");
const postRoute = express.Router();
const Post = require("../models/Post");
const { convertToMongoQuery } = require("../utils/functions");
const { initializeApp } = require("firebase/app");
const {
  getStorage,
  ref,
  getDownloadURL,
  uploadBytesResumable,
} = require("firebase/storage");
const multer = require("multer");
const config = require("../config/firebase.config");
initializeApp(config.firebaseConfig);
const storage = getStorage();

const upload = multer({ storage: multer.memoryStorage() });

async function uploadFile(file, folder) {
  try {
    const storageRef = ref(storage, `${folder}/${file.originalname}`);
    const metadata = {
      contentType: file.mimetype,
    };

    const snapshot = await uploadBytesResumable(
      storageRef,
      file.buffer,
      metadata
    );
    const downloadURL = await getDownloadURL(snapshot.ref);

    console.log("File successfully uploaded.");
    return downloadURL;
  } catch (error) {
    console.log(error)
    return error;
  }
}

// Create new post 
postRoute.route("/").post(
  upload.fields([
    {
      name: "images",
      maxCount: 3,
    },
  ]),
  (req, res, next) => {


    if (!req.files || !req.files["images"]) {
      Object.assign(req.body, {
        images: [],
        added_on: new Date()
      });

      Post.create(req.body)
        .then((data) => {
          res.status(200).json(data);
        })
        .catch((err) => {
          return next(err);
        });
      return;
    }


    const uploadPromises = req.files["images"].map(file =>
      uploadFile(file, "images")
    );

    Promise.all(uploadPromises)
      .then((urls) => {
        Object.assign(req.body, {
          images: urls,
          added_on: new Date()
        });

        Post.create(req.body)
          .then((data) => {
            res.status(200).json(data);
          })
          .catch((err) => {
            return next(err);
          });
      })
      .catch((err) => next(err));
  }
);

// Get all post
postRoute.route("/").get((req, res, next) => {
  const query = {}
  if (req.query.user) query.author = req.query.user
  Post.find(query)
    .then((data) => {
      res.status(200).json(data);
    })
    .catch((err) => {
      return next(err);
    });
});

// get posts for reported lost items
postRoute.route("/lost").get((req, res, next) => {
  const query = { type: false }
  if (req.query.user) query.author = req.query.user
  Post.find(query)
    .then((data) => {
      res.status(200).json(data);
    })
    .catch((err) => {
      return next(err);
    });
});

// get posts for reported found items
postRoute.route("/found").get((req, res, next) => {
  const query = { type: true }
  if (req.query.user) query.author = req.query.user
  Post.find(query)
    .then((data) => {
      res.status(200).json(data);
    })
    .catch((err) => {
      return next(err);
    });
});

// get info for post id
postRoute.route("/:postid").get((req, res, next) => {
  Post.findOne({ _id: req.params.postid })
    .then((post) => {
      if (post) {
        res.status(200).json({
          data: post
        });
      } else {
        res.status(401).json({ result: "Post not found" });
      }
    })
    .catch((err) => {
      res.status(401).json({ result: "Post not found",errorMsg:err });
    });
})


module.exports = postRoute;
