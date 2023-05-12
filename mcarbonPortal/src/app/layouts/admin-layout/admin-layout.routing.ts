import { Routes } from "@angular/router";

import { DashboardComponent } from "../../dashboard/dashboard.component";
import { UserProfileComponent } from "../../user-profile/user-profile.component";
import { UserManagementComponent } from "../../user-management/user-management.component";
import { SessionManagementComponent } from "../../session-management/session-management.component";
import { DynamicSupportComponent } from "../../dynamic-support/dynamic-support.component";
import { AccountManagementComponent } from "app/account-management/account-management.component";

export const AdminLayoutRoutes: Routes = [
  { path: "dashboard", component: DashboardComponent },
  { path: "user-profile", component: UserProfileComponent },
  { path: "account-management", component: AccountManagementComponent },
  { path: "user-management", component: UserManagementComponent },
  { path: "session-management", component: SessionManagementComponent },
  { path: "dynamic-support", component: DynamicSupportComponent },
];
