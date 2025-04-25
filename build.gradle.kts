import toni.blahaj.setup.modCompileOnly
import toni.blahaj.setup.modImplementation

plugins {
	id("toni.blahaj")
}

blahaj {
	config { }
	setup {
		txnilib("1.0.23")

		if (mod.isForge) {
			deps.compileOnly(deps.annotationProcessor("io.github.llamalad7:mixinextras-common:0.4.1")!!)
			deps.implementation(deps.include("io.github.llamalad7:mixinextras-forge:0.4.1")!!)

			deps.modCompileOnly(modrinth("quark", "1.20.1-4.0-462"))
			deps.modCompileOnly(modrinth("zeta", "1.20.1-1.0-30"))
		}
	}
}