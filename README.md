# sw-project

This is a JavaEE project for our course 'Software Development'.

## Notes
- Test User: Email=test@test.test, Password=test
- Collections are loaded in multiple requests by calling root.childCollection.someMethod() in the Controllers,
  this is not very clean but works fine and there seems to be no simple workaround (Eager Fetching has several bugs with multiple collections).
- Bootsfaces components are used for UI styling/elements. The library seems quite active
  and it was used to try out an JavaEE UI library in the project.
  It has some downsides that where discovered during the project, but overall it worked out quite well. 
- Changes to Diagramms
    - Class Diagramm
        - Address was mad embeddable
        - Customer now uses the email (it's unique identifier) as primary key
        - Some Attributes where renamed from snake case to camel case
    - Component Diagram
        - An additional Exception is thrown
        - An additional external service is used (Ads)
    - Usecase Diagram
        - Added Usecase to use the office supply service
        - Added list of all orders ready to be picked up in employee dashboard
- Two external services where wrapped for internal use and not used directly. (Ad- & Mail-Service)
- Element Collections where not needed in the data model and therefore not used. (Embeddables where used instead)
- Repository Classes where not used, as there was not much use for all CRUD actions on all entities.
- A filter was used for restricted access to the customer portal.