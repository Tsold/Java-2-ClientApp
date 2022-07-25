Use Master
go

create DATABASE CarAds
go

use CarAds
go



create table [Role]
(
	IDRole int not null primary key identity(1,1),
	[Type] nvarchar(50) not null
)
go

create table LoginCred
(
	IDLoginCred int not null primary key identity(1,1),
	UserName nvarchar(30) not null,
	[Password] nvarchar(30) not null
)
go



create table [User]
(
	IDUser int not null primary key identity(1,1),
	[Name] nvarchar(50) not null,
	Surname nvarchar(50) not null,
	RoleID int not null references Role(IDRole),
	LoginCredID int not null references LoginCred(IDLoginCred)
)
go

create table VehicleType
(
	IDVehicleType int not null primary key identity(1,1),
	[Type] nvarchar(50) not null
)
go


create table Vehicle (
	IDVehicle int not null primary key identity(1,1),
	VehicleTypeID int not null references VehicleType(IDVehicleType),
	Maker nvarchar(50) not null,
	Model nvarchar(50) not null,
	ProductionYear nvarchar(30) not null,
	InicialKm int not null,
	VehicleAvaible bit
	CONSTRAINT VehicleAvaible_check CHECK (VehicleAvaible IN (1,0))
)
go





create table Advertisement
(
	IDAdvertisement int not null primary key identity(1,1),
	VehicleID int not null references Vehicle(IDVehicle),
	Title NVARCHAR(30) not null,
	Price int not null,
	DateAndTime datetime not null,
	isTaken bit,
	UserID int not null references [User](IDUser),
	CONSTRAINT isTaken_Check CHECK (isTaken IN (1,0))
)
go

create proc DeleteAll
AS
BEGIN

	DELETE from [Advertisement]
	DELETE from [User]
	DELETE from [Role]
	DELETE from LoginCred
	DELETE from Vehicle
	DELETE from [VehicleType]


	DBCC CHECKIDENT ([User], RESEED, 0)
	DBCC CHECKIDENT ([Role], RESEED, 0)
	DBCC CHECKIDENT (LoginCred, RESEED, 0)
	DBCC CHECKIDENT (Vehicle, RESEED, 0)
	DBCC CHECKIDENT ( [VehicleType], RESEED, 0)
	DBCC CHECKIDENT ( Advertisement, RESEED, 0)

END
go


Create proc DataFill
as
BEGIN
 
insert into [Role]([Type]) VALUES('Admin')
insert into [Role]([Type]) VALUES('User')


insert into LoginCred(UserName,[Password]) VALUEs('Admin','Admin1')
insert into [User]([Name],Surname,RoleID,LoginCredID) VALUES('Admin','Adminovic',1,1)


insert into LoginCred(UserName,[Password]) VALUEs('Pero','Peric')
insert into [User]([Name],Surname,RoleID,LoginCredID) VALUES('Pero','Peric',2,2)


insert into LoginCred(UserName,[Password]) VALUEs('Ivo','Ivic')
insert into [User]([Name],Surname,RoleID,LoginCredID) VALUES('Ivo','Ivic',2,3)


insert into VehicleType([Type]) VALUES('Sedan')
insert into VehicleType([Type]) VALUES('Hatchback')
insert into VehicleType([Type]) VALUES('N1')
insert into VehicleType([Type]) VALUES('Wagon')
insert into VehicleType([Type]) VALUES('Coupe')


INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(2,'Audi','A3 1.6 TDI','2013','120000',0)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(1,'Bmw','320D','2013','80000',0)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(5,'Mercedes','C220D','2015','90000',0)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(3,'Renault','1.5 DCI','2019','8000',1)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(4,'VW','Passat 2.0 TDI','2008','180000',1)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(3,'VW','Golf 6 1.6 TDI','2011','20000',1)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(2,'VW','golf 7 1.6 TDI','2014','10000',1)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(2,'Bmw','116D','2012','50000',1)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(1,'Bmw','525D','2013','30000',1)
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(5,'Mercedes','C220D','2016','40000',1)
 

insert into Advertisement(Title,VehicleID,Price,DateAndTime,isTaken,UserID) VALUEs('AUDI a3 1.6 tdi 2013',1,10000,
'2021-04-02 12:00:00',0,1)
insert into Advertisement(Title,VehicleID,Price,DateAndTime,isTaken,UserID) VALUEs('BMW 320d automatik',2,14000,
'2021-08-09 14:00:00',0,1)
insert into Advertisement(Title,VehicleID,Price,DateAndTime,isTaken,UserID) VALUEs('Mercedes C220d coupe ',3,24000,
'2021-04-02 16:30:00',0,1)

END

go    


exec DeleteAll
exec DataFill

select * from Vehicle
select * from Advertisement
GO




go
create proc  ValidateUser
      @UserName NVARCHAR(30),
      @Password NVARCHAR(30)
AS
BEGIN
      SET NOCOUNT ON;
      DECLARE @UserID INT,  @RoleID INT
     
      SELECT @UserID = u.IDUser, @RoleID = u.RoleID
      FROM [User] as u inner join LoginCred as l on l.IDLoginCred = u.LoginCredID WHERE UserName = @UserName AND [Password] = @Password
     
      IF @UserID IS NOT NULL
      BEGIN
            
             select u.IDUser,u.name,u.Surname,u.RoleID from [User] as u WHERE IDUser = @UserID-- User Valid
           
      END
      ELSE
      BEGIN
            SELECT null --User invalid
      END
END
go



Create proc GetAdvertisements
as
BEGIN
SELECT * from Advertisement
END
go



Create proc GetAvailableAds
as
BEGIN
SELECT * from Advertisement where isTaken = 0
END
go


Create proc GetTakenAds
@ID int
as
BEGIN
SELECT * from Advertisement where isTaken = 1 and UserID = @ID
END
go





Create proc GetSingleAd
@ID int
as
BEGIN
SELECT * from Advertisement where Advertisement.IDAdvertisement = @ID
END
go


CREATE proc DeleteAd
@ID int
AS
BEGIN
DELETE Advertisement WHERE IDAdvertisement = @ID
END
GO




create PROC AddAdvertisement
@Title NVARCHAR(30),
@VehicleID int,
@Price int,
@Date datetime,
@isTaken int,
@UserID int
as
BEGIN
INSERT into  Advertisement(Title,VehicleID,Price,DateAndTime,isTaken,UserID) VALUEs(@Title,@VehicleID,@Price,@Date,@isTaken,@UserID)
END
go




Create proc UpdateIsTakenAd
@ID int,    
@UserID int
as
BEGIN
Update Advertisement
set isTaken = 1,  UserID = @UserID
WHERE IDAdvertisement = @ID
END
go


Create proc UpdateIsNotTakenAd
@ID int
as
BEGIN
Update Advertisement
set isTaken = 0
WHERE IDAdvertisement = @ID
END
go

Create proc DeleteAllAds
AS
BEGIN
delete  from Advertisement
DBCC CHECKIDENT ( Advertisement, RESEED, 0)
END
go

Create proc DeleteTakenAds
AS
BEGIN
delete  from Advertisement where Advertisement.isTaken = 1
END
go

alter proc DeleteAllFreeAds
AS
BEGIN
delete  from Advertisement where Advertisement.isTaken = 0
END
go

--Vehicle procedure

Create proc GetVehicles
as
BEGIN
SELECT * from Vehicle
END
go



create proc GetAllVehicles
as
BEGIN
SELECT v.IDVehicle, v.Maker +' '+ v.model as 'MakerModel'  from Vehicle as v
END
go

create proc GetVehicleInfo
@id int
as
BEGIN
SELECT v.IDVehicle, v.VehicleTypeID , v.Maker, v.model,v.ProductionYear,v.InicialKm  from Vehicle as v
WHERE v.IDVehicle = @id
END
go

 

create proc UpdateVehicles
@ID int,
@VehicleType int,
@Maker nvarchar(30),
@Model nvarchar(30),
@ProductionYear nvarchar(30),
@InicialKm nvarchar(30)
AS
BEGIN
UPDATE Vehicle
SET VehicleTypeID = @VehicleType, Maker = @Maker, Model = @Model, ProductionYear= @ProductionYear, InicialKm = @InicialKm
where IDVehicle = @ID
END
go

create PROC AddVehicle
@VehicleType int,
@Maker nvarchar(30),
@Model nvarchar(30),
@ProductionYear nvarchar(30),
@InicialKm nvarchar(30),
@VehicleAvaible BIT
as
BEGIN
INSERT into Vehicle(VehicleTypeID,Maker,Model,ProductionYear,InicialKm,VehicleAvaible) VALUES(@VehicleType,@Maker,@Model,@ProductionYear,@InicialKm,@VehicleAvaible)

END
go



create proc GetVehicleByID
@IDTravelWarrant int
as 
BEGIN
SELECT   v.Maker +' '+ v.model as 'MakerModel' from Vehicle as v
inner JOIN TravelWarrant as t on t.VehicleID = v.IDVehicle
where t.IDTravelWarrant = @IDTravelWarrant
END
go


create proc VehicleTaken
@IDVehicle int
as 
begin
UPDATE Vehicle
set VehicleAvaible = 0
where IDVehicle = @IDVehicle
end

go

create proc VehicleAvaible
@IDVehicle int
as 
begin
UPDATE Vehicle
set VehicleAvaible = 1
where IDVehicle = @IDVehicle
end
go

create proc UpdateVehicleStatus
@IDTravelWarrant int
as
BEGIN
UPDATE Vehicle
set VehicleAvaible = 1  where IDVehicle = (select t.VehicleID from TravelWarrant as t 
inner JOIN Vehicle as v on v.IDVehicle=t.VehicleID
WHERE t.IDTravelWarrant = @IDTravelWarrant)
end
go


CREATE proc DeleteVehicle
@ID int
AS
BEGIN
DELETE VehicleServiced WHERE VehicleID = @ID
DELETE Vehicle where IDVehicle = @ID
END
GO

Create proc DeleteAllVehicles
AS
BEGIN
DECLARE @Identity INT
SELECT @Identity = COUNT(Vehicle.VehicleAvaible) from Vehicle WHERE Vehicle.VehicleAvaible = 0
delete  from Vehicle where Vehicle.VehicleAvaible = 1
DBCC CHECKIDENT ( Vehicle, RESEED,@Identity)
END
GO

Create proc DeleteAllVehicles2
AS
BEGIN
delete  from Vehicle 
DBCC CHECKIDENT ( Vehicle, RESEED,0)
END
go

