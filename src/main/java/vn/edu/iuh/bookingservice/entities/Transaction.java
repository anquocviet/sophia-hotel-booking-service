package vn.edu.iuh.bookingservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import vn.edu.iuh.bookingservice.enums.PaymentMethod;
import vn.edu.iuh.bookingservice.enums.PaymentStatus;

@Getter
@Setter
@Entity
@Table(name = "transactions")
public class Transaction {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false)
  private UUID id;

  @Column(name = "transaction_id", unique = true, nullable = false)
  private String transactionId;

  @OneToOne(orphanRemoval = true)
  @JoinColumn(name = "cart_id")
  private Cart cart;

  private PaymentStatus paymentStatus;

  private PaymentMethod paymentMethod;

  @Column(name = "paid_amount")
  private Double paidAmount;

  @Column(name = "created_at")
  private Timestamp createAt;

  @Column(name = "updated_at")
  private Timestamp updatedAt;

  @Column(name = "deleted_at")
  private Timestamp deletedAt;
}