import { Routes } from "@angular/router";

import { DashboardComponent } from "../../dashboard/dashboard.component";
import { UserProfileComponent } from "../../user-profile/user-profile.component";
import { UserManagementComponent } from "../../user-management/user-management.component";
import { SessionManagementComponent } from "../../session-management/session-management.component";
import { DynamicSupportComponent } from "../../dynamic-support/dynamic-support.component";
import { AccountManagementComponent } from "app/account-management/account-management.component";
import { AccessManagementComponent } from "app/access-management/access-management.component";
import { FeatureManagementComponent } from "app/feature-management/feature-management.component";
import { IcdcManagementComponent } from "app/icdc-management/icdc-management.component";
import { FeedbackFormComponent } from "app/feedback-form/feedback-form.component";

export const AdminLayoutRoutes: Routes = [
  { path: "dashboard", component: DashboardComponent },
  { path: "user_profile", component: UserProfileComponent },
  { path: "customer_management", component: AccountManagementComponent },
  { path: "my_users", component: UserManagementComponent },
  { path: "my_sessions", component: SessionManagementComponent },
  { path: "dynamic_links", component: DynamicSupportComponent },
  { path: "manage_platform_access", component: AccessManagementComponent },
  { path: "manage_platform_feature", component: FeatureManagementComponent },
  { path: "icdc", component: IcdcManagementComponent },
  { path: "icdc/create", component: FeedbackFormComponent }
];
