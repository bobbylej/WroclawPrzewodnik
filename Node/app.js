var express  = require('express');
var app      = express();
var mysql      = require('mysql');
var connection  = require('express-myconnection'); 
var bodyParser = require('body-parser');
var logger = require('./app/classes/logger.js');

var port     = process.env.PORT || 8080;

app.use(express.static(__dirname + '/public'));     
app.use(bodyParser.json());
app.use(bodyParser.urlencoded()); 

//do mysql uzywamy https://github.com/felixge/node-mysql/#install

//single,pool and request 

app.use(
    connection(mysql, {
		host     : 'mysql4.mydevil.net',
		user     : 'm1150_piwko',
		password : 'Piwkopiwko1',
		database : 'm1150_piwko',
		multipleStatements : true
	}, 'pool')
);

require('./app/routes')(app);

var server = app.listen(port, function () {
  console.log('Server is running...');

  logger.log('Server started..');
});