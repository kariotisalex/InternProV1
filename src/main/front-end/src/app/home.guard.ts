import {CanActivateFn} from '@angular/router';
import {inject} from "@angular/core";
import {UserService} from "./services/user.service";
import {NavigationService} from "./services/navigation.service";


export const homeGuard: CanActivateFn = (route, state) => {


  if (inject(UserService).user) {

    return true;

  }else {
    inject(NavigationService).goToLogin();
    return false;
  }

};
