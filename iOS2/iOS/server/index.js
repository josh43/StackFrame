var http = require('http'),
    express = require('express'),
    path = require('path'),
    MongoClient = require('mongodb').MongoClient,
    Server = require('mongodb').Server,
    CollectionDriver = require('./collectionDriver').CollectionDriver,
    FileDriver = require('./fileDriver').FileDriver; //<---
 
var app = express();
app.set('port', process.env.PORT || 3000); 
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');
app.use(express.bodyParser()); // <-- add

var mongoHost = 'localHost';
var mongoPort = 27017;
var fileDriver;  //<--
var collectionDriver;
 
var globalDB;
 var url = 'mongodb://localhost:27017/MyDatabase';
// Use connect method to connect to the Server 
MongoClient.connect(url, function(err, db) {
  console.log("Connected correctly to server");
 db.collection("tire").insert({"yooo":"ThisWillWork!!"},function(err,result){
 		if(err)
 			console.log("weee suck again");
 		else
 			console.log("Succeeeeeesss");
 });
 globalDB = db;
 // insertDocuments(db, function() {
   // updateDocument(db, function() {
  var collection = db.collection('test');
  var doc1 = {'hello':'doc1'};
  var doc2 = {'hello':'doc2'};
  var lotsOfDocs = [{'hello':'doc3'}, {'hello':'doc4'}];

  collection.insert(doc1,function(err,result){
  	if(err)
  		console.log("shiiit")
  	else
  		console.log(result);
  		});

  collection.insert(doc2, {w:1}, function(err, result) {});

  collection.insert(lotsOfDocs, {w:1}, function(err, result) {});
  fileDriver = new FileDriver(db); //<--
  collectionDriver = new CollectionDriver(db);
    
  
});
 

app.use(express.static(path.join(__dirname, 'public')));
 
app.get('/', function (req, res) {
	console.log(req.body);
});
 
app.post('/files', function(req,res) {fileDriver.handleUploadRequest(req,res);});
app.get('/files/:id', function(req, res) {fileDriver.handleGet(req,res);}); 

app.get('/:collection', function(req, res, next) { 
// turns into a json object
	console.log("hit /:collection objct is "); 
   var params = req.params;
   var query = req.query.query; //1
   if (query) {
        query = JSON.parse(query); //2
        collectionDriver.query(req.params.collection, query, returnCollectionResults(req,res)); //3
   } else {
        collectionDriver.findAll(req.params.collection, returnCollectionResults(req,res)); //4
   }
});
 
function returnCollectionResults(req, res) {
    return function(error, objs) { //5
        if (error) { res.send(400, error); }
	        else { 
                    if (req.accepts('html')) { //6
                        res.render('data',{objects: objs, collection: req.params.collection});
                    } else {
                        res.set('Content-Type','application/json');
                        res.send(200, objs);
                }
        }
    }
}
app.get('/register/:username/:password',function(req,res){
	
	var user = req.params.username;
	var pass = req.params.password;
	var theFinal = {"user":user,"pass":pass};
	var loginCollection = globalDB.collection("loginCollection");

	var foundIt = null
	foundIt = loginCollection.findOne({"user":user},function(err, document) {

  		if(err){
  			console.log("Error trying to query for name" + user + "In registering");
   			res.send(200,{"Error":"Invalid login"});   
	   		return;
	   		}
	   	if(document){
	   		console.log("Error User: " + user + "  Already exists");
   			res.send(200,{"Error":"Invalid login"});   
	   		return;
	   		}
	   	// this seems like really shitty code but i dont know how to return something..
	   	/// sadd buut truueu mayne
	   	
	   		loginCollection.insert(theFinal,function(err,doc){
				if(err){
					console.log("Error inserting username and passsword");
					res.send(200,{"Error":"Failed to create Account"});
				}
				else
					console.log("Success creating user : " + user );
			});	
			
			
			var objID = loginCollection.findOne({"user":user},function(error,document){
			if(document){
			 console.log("Sending the user the ObjectID");
      	res.send(350,{"ObjectID":document._id});

      }
			else
				res.send(200,{"Error":"Could not get objectID"});
			});
  			
  	});

	
	
	
	


	
	
	
	
});
app.get('/login/:username/:password',function(req,res){

	console.log("Getting called");
	var user = req.params.username;
	var pass = req.params.password;
	var theFinal = {"user":user,"pass":pass};
	
		var loginCollection = globalDB.collection("loginCollection");
	loginCollection.findOne( theFinal ,function(err,document){
    if(err){
      console.log("Error bad server query")
      res.send(200,{"Error":"Bad server query"});
      }else{ 

        if(document){
        console.log("Successful login for user: " + user);
        res.send(350,{"ObjectID":document._id});
      }else{
      console.log("Error incorrect login info")
      res.send(200,{"Error":"Incorrect login information"});

      }


      }
    });


	
  
 
	
	console.log("User " + user +" logged in with password " + pass);
	
	
	



});
app.post('/post/:chatRoomID',function(req,res){

  console.log("Someone is positng something");
var jData = req.body;
console.log(jData);
  var messageStorage = globalDB.collection("messageStorage" + req.params.chatRoomID);
  if(messageStorage == undefined || messageStorage == null)
    return;
  jData.date_created = new Date();
  messageStorage.insert(jData,function(err,callback){
        if(err){
          console.log("Error writing the message");
                res.send(100, {"Error":"Failed to insert message"});

        }else
          if(jData.user == undefined){
            res.send(300,{"Error":"You are not a valid user brah"});
          }else{
            // user command curl -d '{"MyKey":"Big but soo what"}' -H "Cntent-Type: application/json" http://localhost:3000/post/AZ
            // to post brah
            // You can access json data like jData."key"
            // {"myKey":"holla"}
            // jData.myKey == holla
          console.log("The user : " + jData.user + " has sucesfully submitted a message");
          res.send(300,{"Success sending message":jData});
        }
  });

});
app.get('/recentPosts/:chatRoomID/:numberOfMessages',function(req,res){
// get the most recent posts... requests will be in json 
// nothing is encrypted because FAWK IT u know?!?
// in order to do get requests
// curl -i -H "Accept: application/json" -H "Content-Type: application/json" -X GET http://hostname/resource
// the important parameters is numberOfMessages <----
console.log("Message post being requested");
  var params = req.params;
  var chatRoom = "messageStorage" + params.chatRoomID;
  console.log("chat room : " + chatRoom + " is being queired for :" + params.numberOfMessages + "mesages");
  var location = globalDB.collection(chatRoom);
  if(location == undefined || location == null){
      console.log("Made it here A");

    console.log("Invalid requests to db collection<" + params.chatRoomID + ">being made");
    res.send(200,{"Error":"Invalid chat room" + params.chatRoomID});
    return;
  }else{
    // query the latest messages
    // most be a valid json data
   
    if(params.numberOfMessages > 200 || params.numberOfMessages < 0)
      return;
// make the request for this db
  console.log("Made it here B");
   location.find().sort({"date_created":-1}).limit(50).toArray(function(err,results){
    console.log("Inside of sort");
      if(err){
        console.log("Error retrieving documents ");
              res.send(200,{"Error":"Could not retrieve messages"});

      }
      else{
      res.send(200,results);
    }

    });

  }

});
app.get('/:collection/:entity', function(req, res) { //I
   var params = req.params;
   var entity = params.entity;
   var collection = params.collection;
   if (entity) {
       collectionDriver.get(collection, entity, function(error, objs) { //J
          if (error) { res.send(400, error); }
          else { res.send(200, objs); } //K
       });
   } else {
      res.send(400, {error: 'bad url', url: req.url});
   }
});

app.post('/:collection', function(req, res) { //A
    var object = req.body;
    var collection = req.params.collection;
    collectionDriver.save(collection, object, function(err,docs) {
          if (err) { res.send(400, err); } 
          else { res.send(201, docs); } //B
     });
});

app.put('/:collection/:entity', function(req, res) { //A
    var params = req.params;
    var entity = params.entity;
    var collection = params.collection;
    if (entity) {
       collectionDriver.update(collection, req.body, entity, function(error, objs) { //B
          if (error) { res.send(400, error); }
          else { res.send(200, objs); } //C
       });
   } else {
	   var error = { "message" : "Cannot PUT a whole collection" }
	   res.send(400, error);
   }
});

app.delete('/:collection/:entity', function(req, res) { //A
    var params = req.params;
    var entity = params.entity;
    var collection = params.collection;
    if (entity) {
       collectionDriver.delete(collection, entity, function(error, objs) { //B
          if (error) { res.send(400, error); }
          else { res.send(200, objs); } //C 200 b/c includes the original doc
       });
   } else {
       var error = { "message" : "Cannot DELETE a whole collection" }
       res.send(400, error);
   }
});
 
app.use(function (req,res) {
    res.render('404', {url:req.url});
});

http.createServer(app).listen(app.get('port'), function(){
  console.log('Express server listening on port ' + app.get('port'));
});