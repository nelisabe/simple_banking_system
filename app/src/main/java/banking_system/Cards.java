package banking_system;

import org.sqlite.SQLiteDataSource;

import java.sql.*;

public class Cards {
	private SQLiteDataSource	dataSource;

	Cards(String dbUrl) {
		dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + dbUrl);
		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				statement.execute("CREATE TABLE IF NOT EXISTS card (" +
						"id INTEGER PRIMARY KEY," +
						"number TEXT," +
						"pin TEXT," +
						"balance INTEGER DEFAULT 0" +
						");");
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	public Card 	createNewCard() {
		Card card = new Card();

		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate("INSERT INTO card (number, pin, balance)" +
						"VALUES ('" + card.getCardNumber() + "', '" +
						card.getPin() + "', " + card.getBalance() +
						");");
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		return card;
	}

	public Card 	logIntoCard(String cardNumber, short pin) {
		Card card;
		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet resultSet = statement.executeQuery("SELECT number, pin, balance " +
						"FROM card " +
						"WHERE number = " + cardNumber + " AND " +
						"pin = " + pin + ";")) {
					if (resultSet.next()) {
						card = new Card(resultSet.getString("number"),
								resultSet.getString("pin"),
								resultSet.getInt("balance"));
						return card;
					}
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
					throw new RuntimeException();

				}
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
		throw new RuntimeException();
	}

	public void 	deleteCard(Card card) {
		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate("DELETE FROM card WHERE number = '" +
						card.getCardNumber() + "';");
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	public void 	addIncomeToCard(Card card, int income) {
		card.addBalance(income);
		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				statement.executeUpdate("UPDATE card SET balance = " +
						card.getBalance() + " WHERE number = '" + card.getCardNumber() + "';");
			} catch (Exception ex) {
				System.err.println(ex.getMessage());
			}
		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}
	}

	public void 	checkCardNumber(String cardNumber) {
		if (!Card.isCardNumberValid(cardNumber)) {
			throw new RuntimeException("Probably you made a mistake in the card number.\nPlease try again!");
		}
		try (Connection connection = dataSource.getConnection()) {
			try (Statement statement = connection.createStatement()) {
				try (ResultSet resultSet = statement.executeQuery("SELECT * FROM card WHERE number = '" +
						cardNumber + "';")) {
					if (!resultSet.next()) {
						throw new RuntimeException("Such a card does not exist.");
					}
				} catch (SQLException ex) {
					System.err.println(ex.getMessage());
					throw new RuntimeException();
				}
			} catch (SQLException ex) {
				System.err.println(ex.getMessage());
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}

	public void 	transferMoney(Card fromCard, String toCardNumber, int amount) {
		String		balanceUpdate = "UPDATE card SET balance = balance + ? WHERE number = ?";
		try (Connection connection = dataSource.getConnection()) {
			connection.setAutoCommit(false);
			try (PreparedStatement preparedStatement = connection.prepareStatement(balanceUpdate)) {
				fromCard.rmBalance(amount);
				preparedStatement.setInt(1, -amount);
				preparedStatement.setString(2, fromCard.getCardNumber());
				preparedStatement.executeUpdate();

				preparedStatement.setInt(1, amount);
				preparedStatement.setString(2, toCardNumber);
				preparedStatement.executeUpdate();
				connection.commit();
			} catch (SQLException ex) {
				connection.rollback();
				System.err.println(ex.getMessage());
			}
		} catch (SQLException ex) {
			System.err.println(ex.getMessage());
		}
	}
}
