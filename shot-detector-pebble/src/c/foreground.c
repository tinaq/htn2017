#include <pebble.h>

#define WORKER_ACCEL_EVENT 0

static Window *s_main_window;
static TextLayer *s_output_layer, *s_accel_event_layer;

static void worker_message_handler(uint16_t type, AppWorkerMessage *data){
  if(type == WORKER_ACCEL_EVENT){
    int accel_event = data->data0;
    
    static char s_buffer[32];
    snprintf(s_buffer, sizeof(s_buffer), "%d accel events", accel_event);
    text_layer_set_text(s_accel_event_layer, s_buffer);
  }
}

static void select_click_handler(ClickRecognizerRef recognizer, void *context){
  //check to see if worker is currently active
  bool running = app_worker_is_running();
  
  //toggle running state
  AppWorkerResult result;
  if(running){
    result = app_worker_kill();
    
    if(result == APP_WORKER_RESULT_SUCCESS){
      text_layer_set_text(s_accel_event_layer, "Worker stopped.");
    }
    else {
      text_layer_set_text(s_accel_event_layer, "Error killing worker.");
    }
  }
  else{
    result = app_worker_launch();
    
    if(result == APP_WORKER_RESULT_SUCCESS){
      text_layer_set_text(s_accel_event_layer, "Worker launched.");
    }
    else{
      text_layer_set_text(s_accel_event_layer, "Error launching worker.");
    }
  }
  APP_LOG(APP_LOG_LEVEL_INFO, "Result: %d", result);
}

static void click_config_provider(void *context){
  window_single_click_subscribe(BUTTON_ID_SELECT, select_click_handler);
}

static void main_window_load(Window *window){
  Layer *window_layer = window_get_root_layer(window);
  GRect bounds = layer_get_bounds(window_layer);
  const int inset = 8;
  
  //create UI
  s_output_layer = text_layer_create(bounds);
  text_layer_set_text(s_output_layer, "Use SELECT to start/stop the background worker.");
  text_layer_set_text_alignment(s_output_layer, GTextAlignmentCenter);
  layer_add_child(window_layer, text_layer_get_layer(s_output_layer));

  s_accel_event_layer = text_layer_create(GRect(PBL_IF_RECT_ELSE(5, 0), 135, bounds.size.w, 30));
  text_layer_set_text(s_accel_event_layer, "No data yet.");
  text_layer_set_text_alignment(s_accel_event_layer, PBL_IF_RECT_ELSE(GTextAlignmentLeft, 
                                                                GTextAlignmentCenter));
  layer_add_child(window_layer, text_layer_get_layer(s_accel_event_layer));
}

static void main_window_unload(Window *window){
  text_layer_destroy(s_output_layer);
  text_layer_destroy(s_accel_event_layer);
}

static void init(void){
  s_main_window = window_create();
  window_set_click_config_provider(s_main_window, click_config_provider);
  window_set_window_handlers(s_main_window, (WindowHandlers){
    .load = main_window_load,
    .unload = main_window_unload,
  });
  window_stack_push(s_main_window, true);
  
  //subscribe to worker messages
  app_worker_message_subscribe(worker_message_handler);
}

static void deinit(void){
  window_destroy(s_main_window);
  app_worker_message_unsubscribe();
}

int main(void){
  init();
  app_event_loop();
  deinit();
}


















