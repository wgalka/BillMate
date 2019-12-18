import * as functions from 'firebase-functions'


const admin = require('firebase-admin');
admin.initializeApp();

const db = admin.firestore();

export const onUpdateCollectionBills = functions.firestore.document('groups/{groupID}/bills/{documentID}').onWrite((change, context) => {
        const newVal = change.after.data();
        if (newVal == null) {
            console.log("!newVal jest NULL ")
        } else {
            const documentID = context.params.documentID;
            const groupID = context.params.groupID;

            //sprawdzenie czy wszyscy zaplacili rachunek
            console.log("update val", newVal.billPayers);
            let valuesofpayers = Object.values(newVal.billPayers);
            let allPaid = 0;
            valuesofpayers.forEach(value => {
                if (value == false) {
                    ++allPaid;
                }
            });
            if (allPaid == 0) {
                console.log(">>> Funkcja przenosząca dokument do bookObills zostala wywolana")

                const toref = db.doc('groups/' + groupID + '/bookOfBills/' + newVal.billOwner.toString() + '/bills/' + documentID)
                const fromref = db.doc('groups/' + groupID + '/bills/' + documentID)
                fromref.get().then((doc: any) => {
                        if (!doc.exists) {
                            console.log('No such document:', ' /groups/' + groupID + '/bills/' + documentID);
                        } else {
                            //usuniecie id dokumentu z ksiazeczki rachunkow
                            for (let value in newVal.billPayers) {
                                console.log("<<<<<<< newVal.billpayers", value)
                                let documentRef = db.doc('groups/' + groupID + '/bookOfAccounts/' + value);
                                documentRef.get()
                                    .then((billdoc: any) => {
                                        if (!billdoc.exists) {
                                            // dla tego uzytkownika nie ma jeszcze kolekcji z rachunkami
                                        } else {
                                            let oldbillsdata = billdoc.data();
                                            let setNewData = new Set();

                                            //przepisanie danych do seta
                                            oldbillsdata.idDocs.forEach((oldValue: any) => {
                                                setNewData.add(oldValue);
                                                console.log("for data:", oldValue)
                                            })
                                            setNewData.delete(documentID)

                                            const arrayData: unknown[] = [];

                                            setNewData.forEach(valueinset => {
                                                arrayData.push(valueinset)
                                            });
                                            console.log("setdata: ", setNewData, 'arraydata', arrayData);
                                            const data = {
                                                idDocs: arrayData,
                                                idGroupDoc: groupID,
                                                size: arrayData.length
                                            };

                                            console.log("newData: ", data);
                                            documentRef.set(data).catch((problem: string) => {
                                                console.log("Problem z tworzeniem obiektu w bookOfAcoounts dla: " + value + "error :" + problem)
                                            });

                                            console.log(">> Usuwanie iddokumentu data: ", billdoc.data(), " >>iddokumentu ktory mial byc usuniety:", documentID);
                                            // documentRef.set(data).catch((problem: string) => {
                                            //     console.log("Problem z tworzeniem obiektu w bookOfAcoounts dla: " + value + "error :" + problem)
                                            // });
                                        }
                                    }).catch((err: string) => {
                                        console.log('/// Error getting bookOFAccounts:', err);
                                    });
                            }
                            console.log('Zapisuje dokument w bookOfBills', doc.data());
                            let olddocset = doc.data();
                            let objectKeys = Object.keys(olddocset.billPayers)
                            // let objectValues = Object.values(olddocset.billPayers)

                            let arrayObjectKeys = new Array()
                            // let arrayObjectValues = new Array()

                            objectKeys.forEach(val => {
                                arrayObjectKeys.push(val)
                            })

                            // objectValues.forEach(val=>{
                            //     arrayObjectValues.push(val)
                            // })

                            let arrayOldData = new Array();

                            for (let i = 0; i < arrayObjectKeys.length; i++) {
                                arrayOldData.push(arrayObjectKeys[i])
                            }

                            console.log("<<<<<<< doc billpayers", arrayOldData)
                            // doc.mojalista = arrayOldData;

                            let newObj = {...doc.data(), payersARRAY: arrayOldData}; // { x: number, y: number, z: number }

                            console.log("<<< newObj", newObj);
                            toref.set(newObj).then((r2: any) => {

                                console.log("<<< czy zapis olddata sie udał", r2)

                            });
                            //koniec przeszukania
                            fromref.delete();
                        }
                    }
                )
                    .catch((err: any) => {
                        console.log('Error getting document', err);
                    });

            } else {
                // let path = "groups"+groupID+"bookOfAccounts"
                for (let value in newVal.billPayers) {
                    let documentRef = db.doc('groups/' + groupID + '/bookOfAccounts/' + value);
                    documentRef.get()
                        .then((doc: any) => {
                            if (!doc.exists) {
                                console.log("No such document! Tworze dokument bookOfAcounts dla value=" + value);
                                const data = {
                                    idDocs: [documentID],
                                    idGroupDoc: groupID,
                                    size: 1
                                }
                                documentRef.set(data).catch((problem: string) => {
                                    console.log("Problem z tworzeniem obiektu w bookOfAcoounts dla: " + value + "error :" + problem)
                                });
                            } else {
                                console.log('bookOfAccounts dokumen istnieje dla value=', value, ' Document data:', doc.data(), 'type of:', typeof doc.data());
                                let olddata = doc.data();
                                let setData = new Set();
                                olddata.idDocs.forEach((oldValue: any) => {
                                    setData.add(oldValue);
                                    console.log("for data:", oldValue)
                                })
                                setData.add(documentID);

                                const arrayData: unknown[] = [];

                                setData.forEach(valueinset => {
                                    arrayData.push(valueinset)
                                });
                                console.log("setdata: ", setData, 'arraydata', arrayData);
                                const data = {
                                    idDocs: arrayData,
                                    idGroupDoc: groupID,
                                    size: arrayData.length
                                };
                                console.log("newData: ", data);
                                documentRef.set(data).catch((problem: string) => {
                                    console.log("Problem z tworzeniem obiektu w bookOfAcoounts dla: " + value + "error :" + problem)
                                });
                            }
                        })
                        .catch((err: string) => {
                            console.log('Error getting document', err);
                        });
                }
            }
        }


        return 1


    })
;

export const onCreateBill = functions.firestore.document('groups/{groupID}/bills/{documentID}').onCreate((change, context) => {
    let idDocument = context.params.documentID;
    let idGroup = context.params.groupID;

    let docref = db.doc('groups/' + idGroup + "/bills/" + idDocument);
    docref.set({
        documentID: idDocument
    }, {merge: true});

    console.log("Tworzę bill z id", idDocument, "group id:", idGroup)
    return 1
});

export const onUpdateBookOfBills = functions.firestore.document('groups/{groupID}/bookOfBills/{billownerID}/bills/{documentID}').onUpdate((change, context) => {
    let idGroup = context.params.groupID;
    let idDocument = context.params.documentID;
    let idBillOwner = context.params.billownerID;

    const newVal = change.after.data();

    if (newVal != null) {
        console.log("-> BOOKOFBILLS UPDATE:", newVal)
        let objectKeys = Array.from(Object.values(newVal.billPayers));
        if (objectKeys.includes(false)) {
            console.log("-> wartosci nowego obiektu zawiera false w bill payers", objectKeys);

            let baseref = db.doc('groups/' + idGroup + '/bookOfBills/' + idBillOwner + '/bills/' + idDocument);
            baseref.delete().catch((e: any) => {
                console.log("-> ERROR PRZY USUWANIU:", 'groups/' + idGroup + '/bookOfBills/' + idBillOwner + '/bills/' + idDocument)
            });
            let docref = db.doc('groups/' + idGroup + "/bills/" + idDocument);
            return docref.set(newVal).then(() => {
                console.log('-> Write succeeded!');
            });
        }
        console.log("-> wartosci nowego obiektu", objectKeys)

    } else {
        console.log("-> BOOKOFBILLS UPDATE IS NULL(newVal == null)")
    }

});







