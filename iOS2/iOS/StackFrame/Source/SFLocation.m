//
//  SFLocation.m
//  GeoServices
//
//  Created by Josh  on 9/17/15.
//  Copyright (c) 2015 Josh . All rights reserved.
//

// Apple url tutorial https://developer.apple.com/library/prerelease/ios/documentation/UserExperience/Conceptual/LocationAwarenessPG/MapKit/MapKit.html#//apple_ref/doc/uid/TP40009497-CH3-SW1

#import "SFLocation.h"
@interface SFLocation()
@property(nonatomic,strong) CLLocationManager * locationManger;
@property(nonatomic,strong) CLRegion * arizonaRegion;
@property(nonatomic) int numRegionsMonitoring;
@end

@implementation SFLocation
static const int MAX_REGIONS = 20;

// By enabling location updates on the capabilities of the app I can recieve
// location updates whilst in the background . kewl

-(void)startStandardUpdates{
    
    CLAuthorizationStatus status = [CLLocationManager authorizationStatus];
    
    if(status == kCLAuthorizationStatusDenied || status == kCLAuthorizationStatusNotDetermined ||
       status ==kCLAuthorizationStatusRestricted){
        NSLog(@"Please authorize \n");
        UIAlertView *alertView = [[UIAlertView alloc] initWithTitle:@"USer Locaton"
                                                            message:@"you need to add location services"
                                                           delegate:self
                                                  cancelButtonTitle:@"Cancel"
                                                  otherButtonTitles:@"Settings", nil];
        [alertView show];
    }
    
    /*
     requestAlwaysAuthorization (for background location) or requestWhenInUseAuthorization
     */
    
    if(_locationManger == nil){
       _locationManger = [[CLLocationManager alloc]init];
    }
    
    
    _locationManger.delegate = self;
    _locationManger.desiredAccuracy = kCLLocationAccuracyNearestTenMeters;
    _locationManger.distanceFilter = 100; // In meters
    _locationManger.pausesLocationUpdatesAutomatically = YES;//powers down location hardware when the user
    // is unlinkely to move :DDDD
    // This helps the app manager do similiar things as up there ^
    _locationManger.activityType = CLActivityTypeFitness;
    
    [_locationManger requestAlwaysAuthorization];
    [_locationManger requestWhenInUseAuthorization];
    [_locationManger startUpdatingLocation];
    
    CLLocationCoordinate2D arizonaLocation;
    arizonaLocation.latitude = 32.231218286878239;
    arizonaLocation.longitude = 110.94964715792119;
    float radius = 25; // in km i think
    /*
     latitude	CLLocationDegrees	32.231218286878239	32.231218286878239
     longitude	CLLocationDegrees	-110.94964715792119	-110.94964715792119
     
     */
    _arizonaRegion = [[CLRegion alloc]initCircularRegionWithCenter:arizonaLocation radius:radius identifier:@"pollo"];
    [_locationManger startMonitoringForRegion:_arizonaRegion];
}
-(void)registerRegionWithCircularOverlay:(MKCircle *)overlay andIdentifier:(NSString *)identifier{
    CLLocationDistance radius = overlay.radius; // MEASURED IN METERS
    if(radius > 100){
        NSLog(@"Watchout the radius is greater than 100 \n");
    }
    // else initialize
    CLCircularRegion * geoRegion = [[CLCircularRegion alloc]initWithCenter:overlay.coordinate
                                                                    radius:radius
                                                                identifier:identifier];
    
    // The api says that the class must descend from CLRegion which CLCircularRegion doooes
    // IF you want you can customise which boundary crossing events will njotify the app with notifyOnEntry and
    // notifyOnExit of the clRegion class
    
    
    // if it is than add it to locationManager
    // Note we cannot monitor more than 20 region
    // A very great way around this is to use BSP algorithms. If someone isnt even near a region
    // do not attempt to monitor it DUUHHHH but this is early dev
    if(_numRegionsMonitoring >= MAX_REGIONS){
        NSLog(@"Error we cannot monitor more than %i",MAX_REGIONS);
        return;
    }
  
        
        [_locationManger startMonitoringForRegion:geoRegion];
        _numRegionsMonitoring++;
    
    
    BOOL canWeMonitor = [CLLocationManager isMonitoringAvailableForClass:geoRegion];
    if(!canWeMonitor){
        NSLog(@"Error we cannot monitor this class\n");
    }

        // Note that the user must cross the boundary in order to recieve an event
    
    
}
-(void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region{
    // This will send the region which you have entered this can be usefull for a variety of things IM SURE
    NSString * regionName = [region identifier];
    NSLog(@"You have entered %@\n",regionName);
    
}
-(void)locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region{
    NSString * regionName = [region identifier];
    NSLog(@"You have left %@\n",regionName);
    
}
-(void)startSignificantChangeUpdates{
    if(_locationManger == nil){
        _locationManger = [[CLLocationManager alloc]init];
    }
    _locationManger.delegate = self;
    [_locationManger startUpdatingLocation];
}


-(void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    
}
-(void)locationManager:(CLLocationManager *)manager didFinishDeferredUpdatesWithError:(NSError *)error{
    
    
}
-(void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations{
    // main delegate/interface method to notify updates based on locations
    CLLocation * location = [locations lastObject];
    NSDate * eventDate = location.timestamp;
    NSTimeInterval howRecent = [eventDate timeIntervalSinceNow];
    if(fabs(howRecent) < 15.0){
        // since the event is recent we will do something with it
        NSLog(@"latitude %+.6f, longitude %+.6f\n",
              location.coordinate.latitude, location.coordinate.longitude);
        // For now we just print to sCREEEN OH KAY THAAANKS
    }
}

-(void)locationManagerDidPauseLocationUpdates:(CLLocationManager *)manager{
    //required
}
-(void)locationManagerDidResumeLocationUpdates:(CLLocationManager *)manager{
    //required
}

@end

