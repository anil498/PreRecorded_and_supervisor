import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterModule } from "@angular/router";
import { FormsModule } from "@angular/forms";
import { MatFormFieldModule } from "@angular/material/form-field";
import { FooterComponent } from "./footer/footer.component";
import { NavbarComponent } from "./navbar/navbar.component";
import { SidebarComponent } from "./sidebar/sidebar.component";
import { MatSelectModule } from "@angular/material/select";
import { MatOptionModule } from "@angular/material/core";
import { MatIconModule } from "@angular/material/icon";
import { MatMenuModule } from "@angular/material/menu";
@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    MatSelectModule,
    MatOptionModule,
    MatMenuModule,
    MatIconModule,
    MatFormFieldModule,
  ],
  declarations: [FooterComponent, NavbarComponent, SidebarComponent],
  exports: [FooterComponent, NavbarComponent, SidebarComponent],
})
export class ComponentsModule {}
