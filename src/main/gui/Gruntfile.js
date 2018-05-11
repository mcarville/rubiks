module.exports = function (grunt) {

  grunt.initConfig({
    pkg: grunt.file.readJSON('package.json'),
	clean: {
	  build: {
		src: ["dist"]
	  }
	},
	concat: {
      options: {
        separator: ';'
      },
      basic_and_extras: {
        files: {
          'dist/js/<%= pkg.name %>.js': [
            'app/js/dependencies/angular.js',
            'app/js/dependencies/angular*.js',
            'app/js/dependencies/raphael.js',
            'app/js/dependencies/g.raphael.js',
            'app/js/dependencies/g.*.js',
            'app/js/dependencies/*.js',
			'app/js/*.js',
          ]
        }
      }
    },
    uglify: {
      options: {
        banner: '/*! <%= pkg.name %> <%= grunt.template.today("dd-mm-yyyy") %> */\n',
        compress: false,
        beautify: true
      },
      dist: {
        files: {
          'dist/js/<%= pkg.name %>.min.js': ['dist/js/<%= pkg.name %>.js']
        }
      }
    },
    cssmin: {
      combine: {
        files: {
          'dist/css/<%= pkg.name %>.min.css': [
            'app/css/*.css'
          ]
        }
      }
    },
	copy: {
	  main: {
		files: [
		  // includes files within path
		  {expand: true, cwd: 'app/images/', src: ['**'], dest: 'dist/images', filter: 'isFile'},
		  {expand: true, cwd: 'app/templates/', src: ['**'], dest: 'dist/templates', filter: 'isFile'},
		  {expand: true, cwd: 'app/', src: ['form**.html', 'index.html', '**.ico'], dest: 'dist', filter: 'isFile'},
		  /*
		  // includes files within path and its sub-directories
		  {expand: true, src: ['path/**'], dest: 'dest/'},

		  // makes all src relative to cwd
		  {expand: true, cwd: 'path/', src: ['**'], dest: 'dest/'},

		  // flattens results to a single level
		  {expand: true, flatten: true, src: ['path/**'], dest: 'dest/', filter: 'isFile'},*/
		],
	  },
	},
    qunit: {
      files: ['test/**/*.html']
    },
    jshint: {
      files: ['Gruntfile.js', 'js/**/*.js'],
      options: {
        // options here to override JSHint defaults
        globals: {
          jQuery: true,
          console: true,
          module: true,
          document: true
        }
      }
    },
    watch: {
      minify: {
      files: ['js/**/*.js', 'css/**/*.css', 'directives/**/*.js'],
      tasks: ['concat', 'uglify', 'cssmin']
	  },
	  less: {
        files: ['directives/{,**/}*.less', 'less/{,**/}*.less'],
        tasks: ['less:dev', 'cssmin']
      },
      livereload: {
        options: {
          livereload: 35290
        },
        files: [
          '{,**/}*.html'
        ]
      }
    },
    less: {
      dev: {
        files: {
          "dev/main.css": [
            'directives/{,**/}*.less',
            'less/{,**/}*.less'
          ]
        }
      }
    },

    // The actual grunt server settings
    connect: {
      options: {
        port: 9001,
        // Change this to '0.0.0.0' to access the server from outside.
        hostname: 'localhost',
        livereload: 357290
      },
      proxies: [
        {
          context: '/app/rest',
          host: grunt.option('restServer') || 'localhost',
          port: grunt.option('restPort') || 8080,
          https: false,
          changeOrigin: false,
          xforward: true,
          rewrite: {
            '/app/rest/' : '/rubiks-app/'
          },
          headers: {
            "Host": grunt.option('restServer') + (80 == (grunt.option('restPort') || 8080) ? '' : (grunt.option('restPort') || 8080))
          }
        }
      ],
      livereload: {
        options: {
          open: true,
          middleware: function (connect, options) {
            var middlewares = [];

            if (!Array.isArray(options.base)) {
              options.base = [options.base];
            }

            // Setup the proxy
            middlewares.push(require('grunt-connect-proxy/lib/utils').proxyRequest);

            // Serve static files
            options.base.forEach(function (base) {
              middlewares.push(connect.static(base));
            });

            return middlewares;
          }
        }
      }
    }
  });

  // Load grunt tasks automatically
  require('load-grunt-tasks')(grunt);

  // Time how long tasks take. Can help when optimizing build times
  require('time-grunt')(grunt);

  grunt.loadNpmTasks('grunt-contrib-clean');
  grunt.loadNpmTasks('grunt-contrib-uglify');
  grunt.loadNpmTasks('grunt-contrib-jshint');
  grunt.loadNpmTasks('grunt-contrib-watch');
  grunt.loadNpmTasks('grunt-contrib-concat');
  grunt.loadNpmTasks('grunt-contrib-cssmin');
  grunt.loadNpmTasks('grunt-contrib-copy');
  grunt.loadNpmTasks('grunt-connect-proxy');

  grunt.registerTask('default', [
    'clean:build',
	'concat',
    'uglify',
    'cssmin',
	'copy:main',
  ]);

  /* local server configuration */
  grunt.registerTask('serve', function () {
    grunt.task.run([
	  'configureProxies:server',
      'less:dev',
      'connect:livereload',
      'watch'
    ]);
  });


};