CREATE TABLE Person (
	PersonID 			int PRIMARY KEY,
	isEmployee 		BIT DEFAULT 0
);
#isEmployee, 0 = Customer / 1 = Employee 

CREATE TABLE Reservations (
	BikeID 			int FOREIGN KEY REFERENCES Bikes(BikeID),
	PersonID			int FOREIGN KEY REFERENCES Person(PersonID),
	Date 			varchar(255)
);

CREATE TABLE Bikes (
	BikeID 			int PRIMARY KEY,
	Condition 			varchar(255),
	Model 			varchar(255),
	rentalCost/day 		float,
	repairCost 		float
);

CREATE TABLE Services (
	PersonID			int FOREIGN KEY REFERENCES Person(PersonID),
	BikeID			int FOREIGN KEY REFERENCES Bikes(BikeID),
	isRental 			BIT DEFAULT 1,
	Time 			TIME
);
#isRental, 0 = Repair / 1 = Rental 
#Time, if isRental = 0 then timeBeingRepairedUntil
#Time, if isRental = 1 then timeRentedOutUntil
