//
//  SFLocation.h
//  GeoServices
//
//  Created by Josh  on 9/17/15.
//  Copyright (c) 2015 Josh . All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreLocation/CoreLocation.h>
#import <MapKit/MapKit.h>
@interface SFLocation : NSObject <CLLocationManagerDelegate>

@property(nonatomic) CLAuthorizationStatus status;
// When you do <CCLocation....> this is basically an interfance
// I am conforming the methods that will be called by my CCLocationManager
// And I am ZEEE DELEGATE MUAHAAHHAHA ?????? :|

// Used for intense updates that are very accurate but consumes a great deal of battery
-(void) startStandardUpdates;

// Provides "good enough " accuracy using wifi and eating less battery
-(void) startSignificantChangeUpdates;

-(void) registerRegionWithCircularOverlay:(MKCircle *)overlay andIdentifier:(NSString *) identifier;
-(void)locationManager:(CLLocationManager *)manager didEnterRegion:(CLRegion *)region;
-(void)locationManager:(CLLocationManager *)manager didExitRegion:(CLRegion *)region;
// Remember to include UIRequiredDeviceCapabilities in the app';s Info.plist

@end
