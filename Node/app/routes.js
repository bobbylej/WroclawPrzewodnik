var place = require('./models/place');
var route = require('./models/route');
var event = require('./models/event');
var image = require('./models/photo');
var model = require('./classes/model');

module.exports = function(app) {

  app.get('/', function(req, res) {
    res.sendfile('./public/index.html');
  });

  app.get('/api/manual', function(req, res) {
    res.sendfile('./public/manual.html');
  });

  //app.get('/api/places/getRandomPhoto', place.getRandomPhoto);
  app.get('/api/places', place.getAll);
  app.get('/api/places/:id(\\d+)', place.get);
  app.get('/api/places/:id(\\d+)/types', place.getTypes);
  app.get('/api/places/:id(\\d+)/withTypes', place.getWithTypes);
  app.get('/api/places/:id(\\d+)/withOpeningHours', place.getWithOpeningHours);
  app.get('/api/places/:id(\\d+)/typesId', place.getTypesId);
  app.get('/api/places/update/:update_time', place.getUpdate);

  app.get('/api/routes/:id(\\d+)/withPlaces', route.getWithPlaces);
  app.get('/api/routes/:id(\\d+)/placesId', route.getPlacesId);
  app.get('/api/routes/update/:update_time', route.getUpdate);

  app.get('/api/events/:id(\\d+)/withCategories', event.getWithCategories);
  app.get('/api/events/:id(\\d+)/categoriesId', event.getCategoriesId); 
  app.get('/api/events/update/:update_time', event.getUpdate);


  app.get('/api/:table/update/:update_time', model.getUpdate);
  
  app.get('/api/:table', model.getAll);
  app.get('/api/:table/:key/:value', model.get);
  app.get('/api/:table/:key/:value/with/:table2', model.getWith);
  app.get('/api/:table/:whereKey/:whereValue/:table2/:table2key', model.getLinked); // do ogarnięcia

  app.post('/api/places/create', place.create);
  app.post('/api/events/create', event.create);
  app.post('/api/routes/create', route.create);
};