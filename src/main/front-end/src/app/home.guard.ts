import {CanActivateFn, Router} from '@angular/router';
import {inject} from "@angular/core";
import {UserService} from "./user.service";


export const homeGuard: CanActivateFn = (route, state) => {


  if (inject(UserService).user) {

    return true;

  }else {
    inject(Router).navigateByUrl('/login');
    return false;
  }

};
