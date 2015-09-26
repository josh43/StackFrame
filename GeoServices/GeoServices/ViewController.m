//
//  ViewController.m
//  GeoServices
//
//  Created by Josh  on 9/17/15.
//  Copyright (c) 2015 Josh . All rights reserved.
//

#import "ViewController.h"

@interface ViewController ()

@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    _mapKitView = [[MKMapView alloc]initWithFrame:self.view.frame];
    _mapKitView.mapType = MKMapTypeHybrid;
    
    _mapKitView.showsUserLocation = YES;
    //_mapKitView.userLocationVisible = YES;
   // _mapKitView.userLocation = YES;
    [self.view addSubview:_mapKitView];
    CGPoint point;
    point.x = 0;
    point.y = 300;
    CGSize theSize;
    theSize.height = 50;
    theSize.width = 250;
    CGRect rect;
    rect.size = theSize;
    rect.origin = point;
    _toolBar = [[UIToolbar alloc]init];
    _toolBar.frame = CGRectMake(0, 400, 300, 50);
    NSMutableArray * items = [[NSMutableArray alloc]init];
    _currLocationButton = [[UIBarButtonItem alloc]initWithTitle:@"CurrentLocation" style:
                UIBarButtonItemStylePlain target:self action:@selector(currentLocationClicked:)];
    [_currLocationButton setTitle:@"Holla"];
    
    [items addObject:_currLocationButton];
    [_toolBar setItems:items];
    [self.view addSubview:_toolBar];
    
    
   
   
    /*
     
     
     UIToolbar *toolbar = [[UIToolbar alloc] init];
     toolbar.frame = CGRectMake(0, 0, self.view.frame.size.width, 44);
     NSMutableArray *items = [[NSMutableArray alloc] init];
     [items addObject:[[[UIBarButtonItem alloc] initWith....] autorelease]];
     [toolbar setItems:items animated:NO];
     [items release];
     [self.view addSubview:toolbar];
     [toolbar release];
     
     */
    // Do any additional setup after loading the view, typically from a nib.
}
-(void)currentLocationClicked:(UIBarButtonItem *)sender{
   	float spanX = 0.00725;
    float spanY = 0.00725;
    MKCoordinateRegion region;
    region.center.latitude = _mapKitView.userLocation.coordinate.latitude;
    region.center.longitude = _mapKitView.userLocation.coordinate.longitude;
    region.span.latitudeDelta = spanX;
    region.span.longitudeDelta = spanY;
   // _mapKitView.searchButton.hidden = YES;
    [_mapKitView setRegion:region animated:YES];
    NSLog(@"%@", _mapKitView.userLocation.title);
}
- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}
-(void)updateMessageActivity:(NSString *)message{
    NSArray<UIBarButtonItem *> *barItems = [_toolBar items];
    UIBarButtonItem * item = barItems[0];
    [item setTitle:@"changing view uiBar"];
    
    //[_currLocationButton setTitle:message];
    //[_currLocationButton setTitle:@"oolasdalsdk"];
}

@end
