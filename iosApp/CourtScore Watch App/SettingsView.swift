import SwiftUI
import shared

struct ColorSchemeOption: Identifiable {
    let id = UUID()
    let name: String
    let playerOneColor: Color
    let playerTwoColor: Color
}

struct SettingsView: View {
    @StateObject private var colorSchemeManager = ColorSchemeManager.shared
    @StateObject private var languageManager = LanguageManager.shared
    @StateObject private var scoringTypeManager = ScoringTypeManager.shared

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                Text("language".localized())
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.top, 8)

                ForEach(languageManager.languages) { language in
                    LanguageCard(
                        language: language,
                        isSelected: language.code == languageManager.selectedLanguageCode,
                        onSelect: {
                            languageManager.selectedLanguageCode = language.code
                        }
                    )
                }

                Text("scoring_type".localized())
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.top, 16)

                ForEach(scoringTypeManager.scoringTypes) { scoringType in
                    ScoringTypeCard(
                        scoringType: scoringType,
                        isSelected: scoringType.name == scoringTypeManager.selectedTypeName,
                        onSelect: {
                            scoringTypeManager.selectedTypeName = scoringType.name
                        }
                    )
                }

                Text("color_scheme".localized())
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.top, 16)

                ForEach(colorSchemeManager.colorSchemes) { scheme in
                    ColorSchemeCard(
                        colorScheme: scheme,
                        isSelected: scheme.name == colorSchemeManager.selectedSchemeName,
                        onSelect: {
                            colorSchemeManager.selectedSchemeName = scheme.name
                        }
                    )
                }
            }
            .padding(.horizontal, 16)
            .padding(.bottom, 16)
        }
        .background(Color(hex: "000000"))
    }
}

struct ColorSchemeCard: View {
    let colorScheme: ColorSchemeOption
    let isSelected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: 8) {
                Circle()
                    .fill(colorScheme.playerOneColor)
                    .frame(width: 24, height: 24)
                    .overlay(
                        Circle()
                            .stroke(isSelected ? Color.white : colorScheme.playerOneColor, lineWidth: isSelected ? 2 : 1)
                    )

                Circle()
                    .fill(colorScheme.playerTwoColor)
                    .frame(width: 24, height: 24)
                    .overlay(
                        Circle()
                            .stroke(isSelected ? Color.white : colorScheme.playerTwoColor, lineWidth: isSelected ? 2 : 1)
                    )
            }
            .frame(maxWidth: .infinity)
            .padding(12)
            .background(isSelected ? colorScheme.playerOneColor.opacity(0.2) : Color(hex: "222327"))
            .cornerRadius(12)
        }
        .buttonStyle(.plain)
    }
}

struct LanguageCard: View {
    let language: LanguageOption
    let isSelected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            HStack(spacing: 8) {
                language.flagView
                    .frame(width: 40, height: 30)
                    .clipShape(RoundedRectangle(cornerRadius: 4))
            }
            .frame(maxWidth: .infinity)
            .padding(12)
            .background(isSelected ? Color(hex: "1E8FD5").opacity(0.3) : Color(hex: "222327"))
            .cornerRadius(12)
        }
        .buttonStyle(.plain)
    }
}

struct ScoringTypeCard: View {
    let scoringType: ScoringTypeOption
    let isSelected: Bool
    let onSelect: () -> Void

    var body: some View {
        Button(action: onSelect) {
            VStack(alignment: .leading, spacing: 4) {
                Text(scoringType.name)
                    .font(.system(size: 14, weight: .medium))
                    .foregroundColor(.white)
                Text(scoringType.description)
                    .font(.system(size: 11))
                    .foregroundColor(Color(hex: "aaaab1"))
            }
            .frame(maxWidth: .infinity, alignment: .leading)
            .padding(12)
            .background(isSelected ? Color(hex: "1E8FD5").opacity(0.3) : Color(hex: "222327"))
            .cornerRadius(12)
            .overlay(
                RoundedRectangle(cornerRadius: 12)
                    .stroke(isSelected ? Color.white : Color.clear, lineWidth: isSelected ? 2 : 0)
            )
        }
        .buttonStyle(.plain)
    }
}

