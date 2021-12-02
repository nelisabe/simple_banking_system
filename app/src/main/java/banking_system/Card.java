package banking_system;

import java.util.Random;

public class Card {
	private final int	bankID;
	private final int	accountID;
	private final int	checkSum;
	private final short	pin;
	private int			balance;
	private String		cardNumber;

	Card() {
		Random	random;

		bankID = 400000;
		random = new Random();
		accountID = random.nextInt(900000000) + 100000000;

		checkSum = findCheckSum();
		pin = (short)(random.nextInt(9000) + 1000);
		balance = 0;
		cardNumber = Integer.toString(bankID);
		cardNumber = cardNumber.concat(Integer.toString(accountID));
		cardNumber = cardNumber.concat(Integer.toString(checkSum));
	}

	Card(String cardNumber, String pin, int balance) {
		this.cardNumber = cardNumber;
		this.pin = Short.parseShort(pin);
		this.balance = balance;
		bankID = 0;
		accountID = 0;
		checkSum = 0;
	}

	private int		findCheckSum() {
		int		checkSum = 0;
		int		luhnSum;
		int		bankIdSum;
		int		accountIdSum;

		bankIdSum = 8;
		accountIdSum = findLuhnSum(accountID);
		luhnSum = bankIdSum + accountIdSum;
		checkSum = luhnSum % 10 == 0 ? 0 : 10 - luhnSum % 10;
		return checkSum;
	}

	private static int		findLuhnSum(int number) {
		int		sum = 0;

		for (int i = 0; number != 0; ++i, number /= 10) {
			if (i % 2 == 0) {
				sum += number % 10 * 2;
				if (number % 10 * 2 > 9) {
					sum -= 9;
				}
			} else {
				sum += number % 10;
			}
		}
		return sum;
	}

	@Deprecated
	public boolean	isNumberEqual(String cardNumber) {
		if (cardNumber.length() != 16) {
			return false;
		}
		if (!cardNumber.substring(0, 6).equals(Integer.toString(bankID))) {
			return false;
		}
		if (cardNumber.charAt(15) - '0' != checkSum) {
			return false;
		}
		if (Integer.parseInt(cardNumber.substring(6, 15)) != accountID) {
			return false;
		}
		return true;
	}

	public static boolean	isCardNumberValid(String cardNumber) {
		int		bankId;
		int		accountId;
		int		checkSum;
		int		luhnSum = 0;

		if (cardNumber.length() != 16) {
			return false;
		}
		bankId = Integer.parseInt(cardNumber.substring(0, 6));
		accountId = Integer.parseInt(cardNumber.substring(6, 15));
		checkSum = cardNumber.charAt(15) - '0';
		luhnSum += findLuhnSum(bankId * 10);
		luhnSum += findLuhnSum(accountId);
		luhnSum += checkSum;
		if (luhnSum % 10 != 0) {
			return false;
		}
		return true;
	}

	public String	getCardNumber() {
		return cardNumber;
	}

	public short	getPin() {
		return pin;
	}

	public int		getBalance() {
		return balance;
	}

	public void 	addBalance(int balance) {
		this.balance += balance;
	}

	public void 	rmBalance(int balance) {
		this.balance -= balance;
	}

	@Deprecated
	public boolean	isPinEqual(short pin) {
		return this.pin == pin;
	}
}
