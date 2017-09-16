//
//  ViewController.swift
//  Drink My Pebble
//
//  Created by Phat Tran on 2017-09-16.
//  Copyright © 2017 Phat Tran. All rights reserved.
//

import UIKit
import CoreLocation

class ViewController: UIViewController, CLLocationManagerDelegate {
    var currNumShots: Int = 0
    var maxNumShots: Int = 0
    var locationManager = CLLocationManager()
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    
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
            httpPost(endpoint: "https://jsonplaceholder.typicode.com/posts",
                     jsonData: myData.data(using: .utf8)!)
            print("I'm calling you an Uber")
        } else {
            print("You're under control")
        }
    }
    
    // TODO: Call this when receive a signal from Pebble
    //
    //
    //
    func onShotsCnt() {
        self.currNumShots += 1
        isShotsExceedLimit(numShots: currNumShots, maxShots: maxNumShots)
    }
    
    @IBOutlet var testTextField: UILabel!
    @IBOutlet var DrinkLimitTextField: UITextField!
    
    @IBOutlet var confirmButton: UIButton!
    
    @IBAction func maxShotsConfirmed() {
        if (DrinkLimitTextField.text == "") {
            print("Type in something asshole!")
            return
        }
        if (maxNumShots == 0) {
            testTextField.text = "You haven't set shots limit yet. Please set it."
            return;
        }
        
        self.maxNumShots = Int(DrinkLimitTextField.text!)!
        onShotsCnt()
    }
    
    func dismissKeyboard() {
        view.endEditing(true)
    }
    
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        let userLocation: CLLocation = locations[0]
        latitude = userLocation.coordinate.latitude
        longitude = userLocation.coordinate.longitude
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
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

