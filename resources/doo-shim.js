var success = false;

console.log('Executing doo shim');

doo.runner.set_print_fn_BANG_(function(x) {
  console.log('__doo-message-prefix__' + x);
});

console.log('Set print fn');

doo.runner.set_exit_point_BANG_(function(success) {
  window.success = success;
});

console.log('Invoking doo runner');

doo.runner.run_BANG_();
