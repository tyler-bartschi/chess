package model;

import chess.ChessGame;

public record GameDataNoID(String whiteUsername, String blackUsername, String gameName, ChessGame game) {
}