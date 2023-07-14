import { NgModule } from "@angular/core";
import { RouterModule } from "@angular/router";
import { CommonModule } from "@angular/common";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AdminLayoutRoutes } from "./admin-layout.routing";
import { DashboardComponent } from "../../dashboard/dashboard.component";
import { UserProfileComponent } from "../../user-profile/user-profile.component";
import { UserManagementComponent } from "app/user-management/user-management.component";
import { SessionManagementComponent } from "app/session-management/session-management.component";
import { FormDialogComponent } from "app/form-dialog/form-dialog.component";
import { DeleteDialogComponent } from "app/delete-dialog/delete-dialog.component";
import { DynamicSupportComponent } from "app/dynamic-support/dynamic-support.component";
import { MatDialogModule } from "@angular/material/dialog";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatInputModule } from "@angular/material/input";
import { MatRippleModule } from "@angular/material/core";
import { MatFormFieldModule } from "@angular/material/form-field";
import { MatTooltipModule } from "@angular/material/tooltip";
import { MatSelectModule } from "@angular/material/select";
import { MatCardModule } from "@angular/material/card";
import { MatTableModule } from "@angular/material/table";
import { MatTabsModule } from "@angular/material/tabs";
import { MatDatepickerModule } from "@angular/material/datepicker";
import { MatNativeDateModule } from "@angular/material/core";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatSnackBarModule } from "@angular/material/snack-bar";
import { MatPaginatorModule } from "@angular/material/paginator";
import { MatSortModule } from "@angular/material/sort";
import { MatProgressSpinnerModule } from "@angular/material/progress-spinner";
import { MatMenuModule } from "@angular/material/menu";
import { MatSlideToggleModule } from "@angular/material/slide-toggle";
import { AccountManagementComponent } from "app/account-management/account-management.component";
import { CreateAccountComponent } from "app/create-account/create-account.component";
import { UpdateAccountDialogComponent } from "app/update-account-dialog/update-account-dialog.component";
import { UpdateUserDialogComponent } from "app/update-user-dialog/update-user-dialog.component";
import { ViewAccessDialogComponent } from "app/view-access-dialog/view-access-dialog.component";
import { ViewFeatureDialogComponent } from "app/view-feature-dialog/view-feature-dialog.component";
import { ViewAccountDialogComponent } from "app/view-account-dialog/view-account-dialog.component";
import { ViewUserDialogComponent } from "app/view-user-dialog/view-user-dialog.component";
import { MatFileUploadModule } from "angular-material-fileupload";
import { ViewSessionSettingDialogComponent } from "app/view-session-setting-dialog/view-session-setting-dialog.component";
import { AccessManagementComponent } from "app/access-management/access-management.component";
import { FeatureManagementComponent } from "app/feature-management/feature-management.component";
import { CreateFeatureDialogComponent } from "app/create-feature-dialog/create-feature-dialog.component";
import { CreateAccessDialogComponent } from "app/create-access-dialog/create-access-dialog.component";
import { MatGridListModule } from "@angular/material/grid-list";
import { MatRadioModule } from "@angular/material/radio";
import { UpdateAccessDialogComponent } from "app/update-access-dialog/update-access-dialog.component";
import { UpdateFeatureDialogComponent } from "app/update-feature-dialog/update-feature-dialog.component";
import { FeedbackFormComponent } from "app/feedback-form/feedback-form.component";
import { DragDropModule } from "@angular/cdk/drag-drop";
import { QuestionTemplateComponent } from "app/templates/question-template/question-template.component";
import { AnswerTemplateComponent } from "app/templates/answer-template/answer-template.component";
import { QuestionIconComponent } from "app/question-icon/question-icon.component";
import { SessionJoinDialogComponent } from "app/session-join-dialog/session-join-dialog.component";

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatCheckboxModule,
    MatRadioModule,
    MatDatepickerModule,
    MatGridListModule,
    MatNativeDateModule,
    MatTableModule,
    MatTabsModule,
    MatDialogModule,
    MatIconModule,
    MatCardModule,
    MatButtonModule,
    MatRippleModule,
    MatFormFieldModule,
    MatSelectModule,
    MatTooltipModule,
    MatSortModule,
    MatSnackBarModule,
    MatPaginatorModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatSlideToggleModule,
    MatFileUploadModule,
    DragDropModule,
    RouterModule.forChild(AdminLayoutRoutes),
  ],
  declarations: [
    DashboardComponent,
    UserProfileComponent,
    UserManagementComponent,
    UpdateUserDialogComponent,
    SessionManagementComponent,
    AccountManagementComponent,
    UpdateAccountDialogComponent,
    FormDialogComponent,
    CreateAccountComponent,
    DeleteDialogComponent,
    DynamicSupportComponent,
    ViewAccountDialogComponent,
    ViewUserDialogComponent,
    ViewAccessDialogComponent,
    ViewFeatureDialogComponent,
    ViewSessionSettingDialogComponent,
    AccessManagementComponent,
    FeatureManagementComponent,
    CreateFeatureDialogComponent,
    CreateAccessDialogComponent,
    UpdateAccessDialogComponent,
    UpdateFeatureDialogComponent,
    FeedbackFormComponent,
    QuestionTemplateComponent,
    AnswerTemplateComponent,
    QuestionIconComponent,
    SessionJoinDialogComponent,
  ],
  entryComponents: [FormDialogComponent, DeleteDialogComponent],
})
export class AdminLayoutModule {}
