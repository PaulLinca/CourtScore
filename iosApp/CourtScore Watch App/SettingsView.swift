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

