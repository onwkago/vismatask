# vismatask

Integration with legacy ThirdPartyService

api/getitem endpoint creates an item on ThirdPartyService,
waits for status OK and then returns object to the client.

Load tested, solution can handle 600 simultaneous requests without errors
where both services run on the same machine: 
  cpu cores: 8
  ram: 32gb
