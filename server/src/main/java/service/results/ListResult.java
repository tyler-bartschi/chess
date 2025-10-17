package service.results;

import java.util.Collection;

import model.GameData;

public record ListResult(Collection<GameData> games) {
}
