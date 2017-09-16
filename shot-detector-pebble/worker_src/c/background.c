#include <pebble_worker.h>

#define WORKER_ACCEL_EVENT 0
#define TIMESTAMP_LOG 1

static uint16_t accel_events = 0;
uint32_t num_samples = 3;


static void accel_tap_handler(AccelAxisType axis, int32_t direction) {
  // A tap event occured
  
  //update value
  accel_events++;
  APP_LOG(APP_LOG_LEVEL_INFO, "TOOK A SHOT");
  
  AppWorkerMessage msg_data = {
    .data0 = accel_events
  };
  
  //send data to the foreground app
  app_worker_send_message(WORKER_ACCEL_EVENT, &msg_data);
  
}

bool shot(AccelData *data){
  if(data[0].y>1500) return true;
  else return false;
}


static void accel_data_handler(AccelData *data, uint32_t num_samples){
  
  if(shot(data)){
    APP_LOG(APP_LOG_LEVEL_INFO, "shot");
    accel_events++;
  }
  
  AppWorkerMessage msg_data = {
    .data0 = accel_events
  };
  
  //send data to the foreground app
  app_worker_send_message(WORKER_ACCEL_EVENT, &msg_data);
}

/*
static void accel_data_handler(AccelData *data, uint32_t num_samples) {
  // Read sample 0's x, y, and z values
  int16_t x = data[0].x;
  int16_t y = data[0].y;
  int16_t z = data[0].z;

  // Determine if the sample occured during vibration, and when it occured
  bool did_vibrate = data[0].did_vibrate;
  uint64_t timestamp = data[0].timestamp;

  if(!did_vibrate) {
    // Print it out
    APP_LOG(APP_LOG_LEVEL_INFO, "t: %llu, x: %d, y: %d, z: %d",
                                                          timestamp, x, y, z);
  } else {
    // Discard with a warning
    APP_LOG(APP_LOG_LEVEL_WARNING, "Vibration occured during collection");
  }
}
*/


static void worker_init(){
  accel_data_service_subscribe(num_samples, accel_data_handler);
}

static void worker_deinit(){
  accel_data_service_unsubscribe();
}

int main(void){
  worker_init();
  worker_event_loop();
  worker_deinit();
}