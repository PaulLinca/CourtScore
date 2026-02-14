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

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                Text("Color Scheme")
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.top, 8)

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
        .background(Color(hex: "121214"))
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

