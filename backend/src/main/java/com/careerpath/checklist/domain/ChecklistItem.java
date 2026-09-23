package com.careerpath.checklist.domain;
import jakarta.persistence.*; import java.util.UUID;
@Entity @Table(name="checklist_items",uniqueConstraints=@UniqueConstraint(columnNames={"career_name","order_index"}))
public class ChecklistItem {
 @Id private UUID id; @Column(name="career_name",nullable=false,length=80) private String careerName; @Column(nullable=false,length=140) private String title;
 @Column(nullable=false,length=500) private String description; @Column(name="order_index",nullable=false) private int orderIndex;
 protected ChecklistItem(){} public ChecklistItem(String c,String t,String d,int o){id=UUID.randomUUID();careerName=c;title=t;description=d;orderIndex=o;}
 public UUID getId(){return id;} public String getCareerName(){return careerName;} public String getTitle(){return title;} public String getDescription(){return description;} public int getOrderIndex(){return orderIndex;}
}
