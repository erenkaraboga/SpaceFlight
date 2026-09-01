package com.spaceflight.feature.newsdetail.logic;

import androidx.lifecycle.SavedStateHandle;
import com.spaceflight.core.domain.usecase.ObserveArticleUseCase;
import com.spaceflight.core.domain.usecase.ObserveIsFavoriteUseCase;
import com.spaceflight.core.domain.usecase.RefreshArticleUseCase;
import com.spaceflight.core.domain.usecase.ToggleFavoriteUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Provider;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class NewsDetailViewModel_Factory implements Factory<NewsDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<ObserveArticleUseCase> observeArticleProvider;

  private final Provider<ObserveIsFavoriteUseCase> observeIsFavoriteProvider;

  private final Provider<RefreshArticleUseCase> refreshArticleProvider;

  private final Provider<ToggleFavoriteUseCase> toggleFavoriteProvider;

  private NewsDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ObserveArticleUseCase> observeArticleProvider,
      Provider<ObserveIsFavoriteUseCase> observeIsFavoriteProvider,
      Provider<RefreshArticleUseCase> refreshArticleProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.observeArticleProvider = observeArticleProvider;
    this.observeIsFavoriteProvider = observeIsFavoriteProvider;
    this.refreshArticleProvider = refreshArticleProvider;
    this.toggleFavoriteProvider = toggleFavoriteProvider;
  }

  @Override
  public NewsDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), observeArticleProvider.get(), observeIsFavoriteProvider.get(), refreshArticleProvider.get(), toggleFavoriteProvider.get());
  }

  public static NewsDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<ObserveArticleUseCase> observeArticleProvider,
      Provider<ObserveIsFavoriteUseCase> observeIsFavoriteProvider,
      Provider<RefreshArticleUseCase> refreshArticleProvider,
      Provider<ToggleFavoriteUseCase> toggleFavoriteProvider) {
    return new NewsDetailViewModel_Factory(savedStateHandleProvider, observeArticleProvider, observeIsFavoriteProvider, refreshArticleProvider, toggleFavoriteProvider);
  }

  public static NewsDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      ObserveArticleUseCase observeArticle, ObserveIsFavoriteUseCase observeIsFavorite,
      RefreshArticleUseCase refreshArticle, ToggleFavoriteUseCase toggleFavorite) {
    return new NewsDetailViewModel(savedStateHandle, observeArticle, observeIsFavorite, refreshArticle, toggleFavorite);
  }
}
