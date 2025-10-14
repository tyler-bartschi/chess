package service.results;

import java.util.Collection;

import model.GameData;

public record ListResult(String status, Collection<GameData> games) {
}
