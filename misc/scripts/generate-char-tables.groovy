@Grapes([
        @Grab(group='au.com.bytecode', module='opencsv', version='2.4'),
        @GrabConfig(systemClassLoader = true)
        ])

			
import au.com.bytecode.opencsv.CSVReader
import groovy.xml.MarkupBuilder

/*
 * @author shervinafshar, @date 02/21/2016
 */

tables = [['fname': 'resources/chars-alphabet.csv', 'name': 'Alphabetical characters'],
		  ['fname': 'resources/chars-diacritics.csv', 'name': 'Diacritics'],
		  ['fname': 'resources/chars-numbers.csv', 'name': 'Numeral characters'],
		  ['fname': 'resources/chars-punctuations.csv', 'name': 'Punctuations and symbols'],
		  ['fname': 'resources/chars-control.csv', 'name': 'Control characters']]

tables.each {
	List<String[]> lines = new CSVReader(new FileReader(it['fname']))
	.readAll()
	header = lines[0]
	cols = header.size()
	it['rows'] = []
	lines[1..lines.size()-1].findAll { row ->
		m = [:]
		i = 0
		header.each {
			m[it] = row[i]
			i++
				}
		it['rows'].add(m)
	}
}

tables.each {
	it['rows'].each {
		Boolean hasImage = new File('resources/char-images/'+it['UCS']+'.svg').exists()
		it['hasImage'] = hasImage
	}
}

File fOut = new File('chars-out.html')

StringWriter writer = new StringWriter()
MarkupBuilder markup = new MarkupBuilder(writer)
markup.setDoubleQuotes(true)

markup.section(class: 'appendix', id: "characters-tables") {
    h2 'Characters'
	tables.each {
		h3 it['name']
		rows = it['rows']
		table(class: 'characters') {
			thead {
				tr {
					th (class: 'charColumn', 'Character')
					th (class: 'ucsColumn', 'UCS')
					th (class: 'charnameColumn', 'Name')
					th (class: 'languageColumn', 'Ar')
					th (class: 'languageColumn', 'Fa')
				}
			}
			tbody {
				rows.findAll
				{ row ->
					tr(id: "def_${row['UCS']}") {
						if (row['hasImage']) {
							td (lang: 'ar', class: 'rtlTermCell') {
								img (src: 'images/characters/'+row['UCS']+'.svg', alt: row['Character'], class: 'charimage')
							}
						} else {
							td (lang: 'ar', class: 'rtlTermCell', row['Character'])
						}
						td (class: 'uname', row['UCS'])
						td (class: 'uname', row['Name'])
						td row['Ar']
						td row['Fa']
					}
				}
			}
		}
	}
}

fOut.withWriter('UTF-8') { it.write(writer.toString())}
