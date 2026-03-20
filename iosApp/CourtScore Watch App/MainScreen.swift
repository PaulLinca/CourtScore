import SwiftUI
import shared

struct MainScreen: View {
    @State private var selectedIndex = 0
    @StateObject private var languageManager = LanguageManager.shared

    var menuItems: [(icon: String, label: String, destination: AnyView)] {
        [
            ("tennisball.fill", "new_match".localized(), AnyView(SportSelectView())),
            ("gear", "settings".localized(), AnyView(SettingsView()))
        ]
    }

    // Detect screen size for responsive layout
    private var isSmallWatch: Bool {
        WKInterfaceDevice.current().screenBounds.width <= 176
    }

    private var topPadding: CGFloat { isSmallWatch ? 20 : 8 }
    private var titleFontSize: CGFloat { isSmallWatch ? 22 : 24 }
    private var iconSize: CGFloat { isSmallWatch ? 35 : 40 }
    private var iconFrameSize: CGFloat { isSmallWatch ? 85 : 100 }
    private var tabViewHeight: CGFloat { isSmallWatch ? 110 : 130 }

    init() {
        for familyName in UIFont.familyNames {
            print(familyName)
            for fontName in UIFont.fontNames(forFamilyName: familyName) {
                print("------ \(fontName)")
            }

        }
        
    }

    var body: some View {
        
        NavigationStack {
            VStack(spacing: 0) {
                Spacer()
                    .frame(height: topPadding)

                Text("Court Score")
                    .font(.collegeClean(size: titleFontSize))
                    .foregroundColor(.white)

                TabView(selection: $selectedIndex) {
                    ForEach(0..<menuItems.count, id: \.self) { index in
                        NavigationLink(destination: menuItems[index].destination) {
                            Image(systemName: menuItems[index].icon)
                                .font(.system(size: iconSize))
                                .foregroundColor(Color(hex: "1E8FD5"))
                                .frame(width: iconFrameSize, height: iconFrameSize)
                                .background(Color.clear)
                                .clipShape(Circle())
                                .overlay(Circle().stroke(Color(hex: "1E8FD5"), lineWidth: 2))
                        }
                        .buttonStyle(.plain)
                        .tag(index)
                    }
                }
                .tabViewStyle(.page)
                .frame(height: tabViewHeight)

                Spacer(minLength: 10)

                Text(menuItems[selectedIndex].label)
                    .font(.headline)
                    .foregroundColor(.white)
                    .animation(.easeInOut, value: selectedIndex)
            }
            .background(Color.black)
        }
    }
}

extension Color {
    init(hex: String) {
        let scanner = Scanner(string: hex)
        var rgbValue: UInt64 = 0
        scanner.scanHexInt64(&rgbValue)
        
        let r = Double((rgbValue & 0xFF0000) >> 16) / 255.0
        let g = Double((rgbValue & 0x00FF00) >> 8) / 255.0
        let b = Double(rgbValue & 0x0000FF) / 255.0
        
        self.init(red: r, green: g, blue: b)
    }
}

extension Font {
    static func collegeClean(size: CGFloat) -> Font {
        return .custom("College Clean Italic", size: size)
    }
}

