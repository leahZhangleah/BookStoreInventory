# BookStoreInventory
This app is mainly about SQlite Database.
It uses a Custom class(Contract) to store static final strings and uri info.
It also uses SQliteOenHelper and ContentProvider to deal with database CRUD(Create,Read,Update,Delete).
Inside ContentProvider, it has notify change set, which will update the main_activity interface timely.

The capture/pick photo function is new in this app. Captured photo will be saved in app's internal storage.
There's also a custom class and function which will load big Bitmaps more efficiently.

<img src="/images/bookstoreinventory1.png" width="120" height="213"/><img src="/images/bookstoreinventory2.png" width="120" height="213"/><img src="/images/bookstoreinventory3.png" width="120" height="213"/><img src="/images/bookstoreinventory4.png" width="120" height="213"/>

Things to improve:

1 The UI, especially the detail and edit(add) acitivity UI are still very ugly, need to be improved

2 How to save images more efficiently.

a.if the image is picked from the gallery, and user deletes the photo in the gallery, the image in this app will be gone too.
so need to save a copy somewhere else.

b.If the app's data is deleted in the setting, all info will be gone. Need to be changed like a

3 How to resize the image when picking or capturing, like Contacts app in iphone
