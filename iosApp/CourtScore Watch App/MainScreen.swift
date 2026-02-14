import SwiftUI
import shared

struct MainScreen: View {
    @State private var selectedIndex = 0
    
    let menuItems: [(icon: String, label: String, destination: AnyView)] = [
        ("tennisball.fill", "New Match", AnyView(MatchView())),
        ("gear", "Settings", AnyView(SettingsView()))
    ]
    
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
            VStack(spacing: 10) {
                Text("Court Score")
                    .font(.collegeClean(size: 24))
                    .foregroundColor(.white)


                TabView(selection: $selectedIndex) {
                    ForEach(0..<menuItems.count, id: \.self) { index in
                        NavigationLink(destination: menuItems[index].destination) {
                            Image(systemName: menuItems[index].icon)
                                .font(.system(size: 40))
                                .foregroundColor(Color(hex: "1E8FD5"))
                                .frame(width: 100, height: 100)
                                .background(Color.clear)
                                .clipShape(Circle())
                                .overlay(
                                    Circle()
                                        .stroke(Color(hex: "1E8FD5"), lineWidth: 2)
                                )
                        }
                        .buttonStyle(.plain)
                        .tag(index)
                    }
                }
                .tabViewStyle(.page)
                .frame(height: 150)

                Text(menuItems[selectedIndex].label)
                    .font(.headline)
                    .foregroundColor(.white)
                    .animation(.easeInOut, value: selectedIndex)
            }
            .padding()
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

