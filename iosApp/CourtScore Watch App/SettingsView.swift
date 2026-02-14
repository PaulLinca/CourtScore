import SwiftUI
import shared

struct ColorSchemeOption: Identifiable {
    let id = UUID()
    let name: String
    let playerOneColor: Color
    let playerTwoColor: Color
}

struct SettingsView: View {
    @AppStorage("selectedColorScheme") private var selectedScheme = "Blue & Orange"

    let colorSchemes: [ColorSchemeOption] = [
        ColorSchemeOption(name: "Teal & Coral", playerOneColor: Color(hex: "008080"), playerTwoColor: Color(hex: "FF7F50")),
        ColorSchemeOption(name: "Blue & Orange", playerOneColor: Color(hex: "5076FF"), playerTwoColor: Color(hex: "F8A464")),
        ColorSchemeOption(name: "Red & Cream", playerOneColor: Color(hex: "A4193D"), playerTwoColor: Color(hex: "FFDFB9")),
        ColorSchemeOption(name: "Pink & Yellow", playerOneColor: Color(hex: "d3687f"), playerTwoColor: Color(hex: "CBCE91")),
        ColorSchemeOption(name: "Cyan & Magenta", playerOneColor: Color(hex: "00BCD4"), playerTwoColor: Color(hex: "E91E63")),
        ColorSchemeOption(name: "Yellow & Red", playerOneColor: Color(hex: "FDD20E"), playerTwoColor: Color(hex: "c72d1b")),
        ColorSchemeOption(name: "Indigo & Lime", playerOneColor: Color(hex: "4831D4"), playerTwoColor: Color(hex: "CCF381"))
    ]

    var body: some View {
        ScrollView {
            VStack(spacing: 12) {
                Text("Color Scheme")
                    .font(.headline)
                    .foregroundColor(.white)
                    .padding(.top, 8)

                ForEach(colorSchemes) { scheme in
                    ColorSchemeCard(
                        colorScheme: scheme,
                        isSelected: scheme.name == selectedScheme,
                        onSelect: {
                            selectedScheme = scheme.name
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

