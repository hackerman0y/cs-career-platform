package com.careerpath.checklist.domain;
import com.careerpath.auth.domain.AppUser; import jakarta.persistence.*; import java.time.Instant; import java.util.UUID;
@Entity @Table(name="checklist_progress",uniqueConstraints=@UniqueConstraint(columnNames={"user_id","item_id"}))
public class ChecklistProgress {
 @Id private UUID id; @ManyToOne(optional=false) @JoinColumn(name="user_id") private AppUser user; @ManyToOne(optional=false) @JoinColumn(name="item_id") private ChecklistItem item;
 @Column(nullable=false) private boolean completed; private Instant completedAt; protected ChecklistProgress(){} public ChecklistProgress(AppUser u,ChecklistItem i){id=UUID.randomUUID();user=u;item=i;}
 public void setCompleted(boolean c){completed=c;completedAt=c?Instant.now():null;} public ChecklistItem getItem(){return item;} public boolean isCompleted(){return completed;}
}
