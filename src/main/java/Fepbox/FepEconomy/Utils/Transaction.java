package Fepbox.FepEconomy.Utils;

public record Transaction(double amount, String sender, String receiver, String status, Long timestamp) {
}
