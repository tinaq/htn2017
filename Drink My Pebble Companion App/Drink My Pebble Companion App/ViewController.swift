//
//  ViewController.swift
//  Drink My Pebble
//
//  Created by Phat Tran on 2017-09-16.
//  Copyright © 2017 Phat Tran. All rights reserved.
//

import UIKit
import CoreLocation
import PebbleKit

class ViewController: UIViewController, CLLocationManagerDelegate, PBPebbleCentralDelegate {
    var connectedWatch: PBWatch = NSObject() as! PBWatch
    
    var currNumShots: Int = 0
    var maxNumShots: Int = 0
    var orderUberEndpoint = "https://jsonplaceholder.typicode.com/posts"
    
    var locationManager = CLLocationManager()
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    
    let myAppUUID = "72a2bcff-7318-487f-b784-1c4f3463ffb0"
    var pebbleManager = PBPebbleCentral.default()
    
    @IBOutlet var outputTextField: UILabel!
    
    func httpPost(endpoint: String, jsonData: Data) {
        if !jsonData.isEmpty {
            var request = URLRequest(url: URL(string: endpoint)!)
            request.httpMethod = "POST"
            request.httpBody = jsonData
            
            URLSession.shared.getAllTasks { (openTasks: [URLSessionTask]) in
                print("open tasks: \(openTasks)")
            }
            
            URLSession.shared.dataTask(with: request) { data, response, error in
                guard let data = data, error == nil else { return }
                do {
                    let json = try JSONSerialization.jsonObject(with: data, options: .allowFragments) as! [String:Any]
                    print(json)
                } catch let error as NSError {
                    print(error)
                }
                }.resume()
        }
    }
    
    func isShotsExceedLimit(numShots: Int, maxShots: Int) {
        print("")
        print("currNumShots: " + String(numShots))
        print("maxNumShots: " + String(maxShots))
        if (numShots > maxShots) {
            // TODO: send request to order an Uber
            //
            //
            //
            let myData = "title=test&body=testbody&userId=101"
            httpPost(endpoint: orderUberEndpoint,
                     jsonData: myData.data(using: .utf8)!)
            outputTextField.text = "You're drunk, calling you an Uber"
        }
    }
    
    func onShotsCnt() {
        currNumShots += 1
        isShotsExceedLimit(numShots: currNumShots, maxShots: maxNumShots)
    }
    
    @IBOutlet var DrinkLimitTextField: UITextField!
    
    @IBOutlet var confirmButton: UIButton!
    
    @IBAction func maxShotsConfirmed() {
        if (DrinkLimitTextField.text == "") {
            outputTextField.text = "Type in something asshole!"
            return
        }
        
        maxNumShots = Int(DrinkLimitTextField.text!)!
        
        if (maxNumShots <= 0) {
            outputTextField.text = "Invalid shots limit."
            return;
        }
        
        outputTextField.text = ""
        
    }
    
    func dismissKeyboard() {
        view.endEditing(true)
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation: CLLocation = locations[0]
        latitude = userLocation.coordinate.latitude
        longitude = userLocation.coordinate.longitude
    }
    
    func pebbleCentral(_ central: PBPebbleCentral, watchDidConnect watch: PBWatch, isNew: Bool) {
        print(watch.name)
        connectedWatch = watch
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(target: self, action: #selector(UIInputViewController.dismissKeyboard))
        view.addGestureRecognizer(tap)
        
        locationManager.delegate = self
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()
        
        pebbleManager.delegate = self
        pebbleManager.appUUID = UUID.init(uuidString: myAppUUID)
        pebbleManager.run()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

